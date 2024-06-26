package script

import (
	"bytes"
	"encoding/json"
	"errors"
	"fmt"
	"slices"
	"strings"
	"time"

	log "github.com/sirupsen/logrus"
)

type ScriptJobRunnerCustomizer func(*ScriptJobRunner) error

type ScriptJobRunner struct {
	commandNsToHandlerMap map[string]ICommandHandler
}

func NewScriptJobRunner() *ScriptJobRunner {
	runner := ScriptJobRunner{}
	runner.commandNsToHandlerMap = make(map[string]ICommandHandler)

	return &runner
}

func (runner *ScriptJobRunner) RegisterCommandHandler(commandHandler ICommandHandler) error {
	ns := commandHandler.GetCommandNamespace()
	if _, ok := runner.commandNsToHandlerMap[ns]; ok {
		return fmt.Errorf("namespace %s already registered", ns)
	}

	runner.commandNsToHandlerMap[ns] = commandHandler

	return nil
}

func (runner *ScriptJobRunner) ParseScript(script string) (*ScriptRootNode, error) {
	scriptRootNode := &ScriptRootNode{}

	var scriptMap map[string]any

	err := json.Unmarshal([]byte(script), &scriptMap)

	if err != nil {
		return nil, err
	}

	{
		value, ok := scriptMap["name"]

		if !ok {
			return nil, errors.New("must specify name")
		}

		scriptName, ok := value.(string)

		if !ok {
			return nil, errors.New("name is not a string")
		}

		if scriptName == "" {
			return nil, errors.New("name is empty")
		}

		scriptRootNode.ScriptName = scriptName
	}

	{
		value, ok := scriptMap["maxCommandSeq"]

		if ok {
			maxCommandSeq, ok := value.(float64)

			if !ok {
				return nil, errors.New("maxCommandSeq is not a float64")
			}

			scriptRootNode.MaxCommandSeq = int(maxCommandSeq)
		}
	}

	rootBlockName := "root"
	scriptRootNode.Name = rootBlockName
	scriptRootNode.Path = rootBlockName

	err = runner.parseNode(&scriptRootNode.ScriptNode, scriptMap)

	if err != nil {
		return nil, err
	}

	_, ok := scriptMap["finallyNode"]

	if ok {
		finallyNodeObj, ok := scriptMap["finallyNode"].(map[string]any)

		if !ok {
			return nil, errors.New("finallyNode is not a obj")
		}

		finallyScriptNode := new(ScriptNode)

		finallyScriptNode.Name = "finallyRoot"
		finallyScriptNode.Path = "finallyRoot"

		scriptRootNode.FinallyNode = finallyScriptNode

		err = runner.parseNode(finallyScriptNode, finallyNodeObj)

		if err != nil {
			return nil, err
		}
	}

	return scriptRootNode, nil
}

func (runner *ScriptJobRunner) RunScript(runCtx *ScriptRunContext) {
	curTime := time.Now()
	runSummary := ScriptRunSummary{
		RunId:      runCtx.RunId,
		CreateTime: curTime,
		UpdateTime: curTime,
		Status:     Status_Running,
	}

	runCtx.RunSummary = &runSummary

	runner.initCurrentScriptNode(runCtx, &runCtx.rootScriptNode.ScriptNode)
	scriptJobsActive.Inc()
	defer scriptJobsActive.Dec()

	for !runCtx.ScriptEnd {
		runner.runNextCommand(runCtx)
	}
}

func (runner *ScriptJobRunner) NotifyCommandOk(runCtx *ScriptRunContext, nextHopInfo *NextHopInfo) {
	runCtx.notifiedCommandEnd = true

	if nextHopInfo != nil {
		if nextHopInfo.NextCommandIdx >= 0 {
			runCtx.currentCommandIdx = nextHopInfo.NextCommandIdx
			return
		}
	}

	runner.onStepEndOk(runCtx)
}

func (runner *ScriptJobRunner) parseNode(scriptNode *ScriptNode, jsonMap map[string]any) error {
	nodePath := scriptNode.Path

	nodes, nodesOk := jsonMap["nodes"]
	commands, commandsOk := jsonMap["commands"]

	if (nodesOk && commandsOk) || (!nodesOk && !commandsOk) {
		return fmt.Errorf("node %s: should specify one and only one of nodes and commands", nodePath)
	}

	if nodesOk {
		nodeRawList, ok := nodes.([]any)

		if !ok {
			return fmt.Errorf("node %s: nodes field is not array", nodePath)
		}

		if len(nodeRawList) == 0 {
			return fmt.Errorf("node %s: nodes empty", nodePath)
		}

		scriptNodeList := make([]*ScriptNode, 0, len(nodeRawList))

		for idx, nodeRaw := range nodeRawList {
			nodeJsonMap, ok := nodeRaw.(map[string]any)

			if !ok {
				return fmt.Errorf("node %s: nodes field idx %d is not a map", nodePath, idx)
			}

			_, ok = nodeJsonMap["name"]

			if !ok {
				return fmt.Errorf("node %s: nodes field idx %d has no name field", nodePath, idx)
			}

			subNodeName, ok := nodeJsonMap["name"].(string)

			if !ok {
				return fmt.Errorf("node %s: nodes field idx %d name field is not string", nodePath, idx)
			}

			subNode := ScriptNode{}
			subNode.Name = subNodeName
			subNode.Path = nodePath + "/" + subNodeName
			subNode.parent = scriptNode

			scriptNodeList = append(scriptNodeList, &subNode)

			err := runner.parseNode(&subNode, nodeJsonMap)

			if err != nil {
				return err
			}
		}

		scriptNode.NodeList = scriptNodeList

		return nil
	} else {
		commandRawList, ok := commands.([]any)

		if !ok {
			return fmt.Errorf("node %s: commands field is not array", nodePath)
		}

		err := runner.parseCommands(scriptNode, commandRawList)

		return err
	}
}

func (runner *ScriptJobRunner) parseCommands(scriptNode *ScriptNode, commandRawList []any) error {
	nodePath := scriptNode.Path

	blockContextMap := make(map[string]*BlockContext)
	commandList := make([]ICommand, 0, len(commandRawList))
	commandDisplayList := make([]string, 0, len(commandRawList))
	commandNsList := make([]string, 0, len(commandRawList))
	catchAjustMap := make(map[int]int)

	scriptNode.BlockContextMap = blockContextMap

	blockNames := make(map[string]bool)
	blockNames["root"] = true
	blockContextList := make([]*BlockContext, 0)
	var blockContext *BlockContext

	catchPointIndexes := make([]int, 0)
	catchPointBlockNames := make([]string, 0)

	for idx, commandRaw := range commandRawList {
		commandLogId := fmt.Sprintf("node %s command at index %d:", nodePath, idx)

		commandObj, ok := commandRaw.(map[string]any)

		if !ok {
			return errors.New(commandLogId + " is not a object")
		}

		commandIdRaw, ok := commandObj["commandId"]

		if !ok {
			return errors.New(commandLogId + " miss field commandId")
		}

		commandId, ok := commandIdRaw.(string)

		if !ok {
			return errors.New(commandLogId + " commandId is not string")
		}

		commandNs, _, ok := strings.Cut(commandId, "/")

		if !ok {
			return errors.New(commandLogId + " no commandSpace prefix found")
		}

		commandHandler, ok := runner.commandNsToHandlerMap[commandNs]

		if !ok {
			return fmt.Errorf("%s no handler for command %s", commandLogId, commandId)
		}

		b := new(bytes.Buffer)
		jsonEncoder := json.NewEncoder(b)
		jsonEncoder.SetEscapeHTML(false)
		jsonEncoder.SetIndent("", "  ")
		jsonEncoder.Encode(commandRaw)
		data := b.Bytes()

		commandDisplayList = append(commandDisplayList, string(data))
		commandNsList = append(commandNsList, commandNs)

		command, err := commandHandler.ParseCommand(commandId, data)

		if err != nil {
			return fmt.Errorf("%s failed to parse, %w", commandLogId, err)
		}

		if len(catchPointIndexes) > 0 {
			command.SetCatchPointIndex(catchPointIndexes[len(catchPointIndexes)-1])
		} else {
			command.SetCatchPointIndex(-1)
		}

		if command.IsCatchPoint() {
			catchBlockName := "root"
			if len(blockContextList) > 0 {
				catchBlockName = blockContextList[len(blockContextList)-1].BlockName
			}

			replaced := false

			if len(catchPointIndexes) > 0 {
				if catchPointBlockNames[len(catchPointBlockNames)-1] == catchBlockName {
					replaced = true
					catchPointIndexes[len(catchPointIndexes)-1] = idx
				}
			}

			if !replaced {
				catchPointIndexes = append(catchPointIndexes, idx)
				catchPointBlockNames = append(catchPointBlockNames, catchBlockName)
			}
		}

		blockCommand, ok := command.(IBlockCommand)

		if ok {
			blockName := blockCommand.GetBlockName()

			if blockName == "" {
				return fmt.Errorf("%s commandId %s blockName empty",
					commandLogId, commandId)
			}

			if blockCommand.IsBlockBegin() {
				if blockNames[blockName] {
					return fmt.Errorf("%s commandId %s blockName %s conflict",
						commandLogId, commandId, blockName)
				}

				blockNames[blockName] = true

				blockContext = new(BlockContext)
				blockContext.BlockName = blockName
				blockContext.StartIdx = idx
				blockContext.CommandId = commandId

				blockContextMap[blockName] = blockContext
				blockContextList = append(blockContextList, blockContext)

				if command.IsCatchPoint() {
					needAdjust := true

					blockCatchGotoEnd, ok := command.(IBlockCatchGotoEnd)
					if ok {
						if !blockCatchGotoEnd.BlockCatchGotoEnd() {
							needAdjust = false
						}
					}

					if needAdjust {
						catchAjustMap[idx] = -1
					}
				}
			} else if blockCommand.IsBlockEnd() {
				if len(blockContextList) == 0 {
					return fmt.Errorf("%s commandId %s blockName %s mismatch",
						commandLogId, commandId, blockName)
				}

				blockContext = blockContextList[len(blockContextList)-1]
				blockContextList = blockContextList[:len(blockContextList)-1]

				if blockName != blockContext.BlockName {
					return fmt.Errorf("%s commandId %s blockName %s mismatch",
						commandLogId, commandId, blockName)
				}

				blockContext.EndIdx = idx

				if len(catchPointBlockNames) > 0 && catchPointBlockNames[len(catchPointBlockNames)-1] == blockName {
					catchPointIndexes = catchPointIndexes[:len(catchPointIndexes)-1]
					catchPointBlockNames = catchPointBlockNames[:len(catchPointBlockNames)-1]
				}

				_, exist := catchAjustMap[blockContext.StartIdx]
				if exist {
					catchAjustMap[blockContext.StartIdx] = idx
				}
			} else {
				return fmt.Errorf("%s commandId %s blockName %s is neither begin nor end",
					commandLogId, commandId, blockName)
			}
		}

		commandList = append(commandList, command)
	}

	if len(commandList) == 0 {
		return fmt.Errorf("%s, commands empty", nodePath)
	}

	for _, command := range commandList {
		if command.GetCatchPointIndex() < 0 {
			continue
		}
		newIdx, exist := catchAjustMap[command.GetCatchPointIndex()]
		if exist {
			command.SetCatchPointIndex(newIdx)
		}
	}

	scriptNode.CommandList = commandList
	scriptNode.CommandDisplayList = commandDisplayList
	scriptNode.CommandNsList = commandNsList

	if len(blockContextList) > 0 {
		blockName := blockContextList[len(blockContextList)-1].BlockName

		return fmt.Errorf("%s, incomplete block, blockName %s",
			nodePath, blockName)
	}

	return nil
}

func (runner *ScriptJobRunner) runNextCommand(runCtx *ScriptRunContext) {
	currentScriptNode := runCtx.CurrentScriptNode

	commandList := currentScriptNode.CommandList
	commandDisplayList := currentScriptNode.CommandDisplayList
	commandNsList := currentScriptNode.CommandNsList
	currentCommandIdx := runCtx.currentCommandIdx

	if runCtx.StopRequested.Load() && !runCtx.FinallyNodeReached {
		runCtx.FinallyNodeReached = true
		runCtx.JobStatus = Status_Stopping
		runCtx.Detail = ""

		runCtx.CurrentScriptNode = nil
		runner.initCurrentScriptNodeForFinally(runCtx)

		if runCtx.CurrentScriptNode == nil {
			runCtx.JobStatus = Status_Stopped
			runner.onScriptEnd(runCtx)
		} else {
			runner.runNextCommand(runCtx)
		}

		return
	}

	if !runCtx.FinallyNodeReached && runCtx.maxCommandSeq > 0 &&
		(runCtx.currentCommandSeq+1) > runCtx.maxCommandSeq {
		runCtx.FinallyNodeReached = true
		runCtx.JobStatus = Status_Failed
		runCtx.Detail = "MaxCommandSeq exceeded"

		runCtx.CurrentScriptNode = nil
		runner.initCurrentScriptNodeForFinally(runCtx)

		if runCtx.CurrentScriptNode == nil {
			runner.onScriptEnd(runCtx)
		} else {
			runner.runNextCommand(runCtx)
		}

		return
	}

	runCtx.currentCommandSeq += 1
	currentCommand := commandList[currentCommandIdx]

	runSummary := *runCtx.RunSummary

	runSummary.CurrentCommandIdx = currentCommandIdx
	runSummary.CurrentCommand = commandDisplayList[currentCommandIdx]
	runSummary.UpdateTime = time.Now()
	runSummary.CurrentCommandSeq = runCtx.currentCommandSeq
	runSummary.ExtendInfo = ""

	runCtx.RunSummary = &runSummary
	runCtx.LogPrefix = fmt.Sprintf("%s Command Seq: %d, path %s, idx %d: commandId %s",
		runCtx.GetTag(), runCtx.currentCommandSeq,
		currentScriptNode.Path, runCtx.currentCommandIdx,
		currentCommand.GetCommandId())

	commandNs := commandNsList[currentCommandIdx]
	commandHandler := runner.commandNsToHandlerMap[commandNs]

	if runCtx.IsInfoEnabled() {
		log.Infof("%s, commandBody: %s", runCtx.LogPrefix, commandDisplayList[currentCommandIdx])
	}

	runCtx.notifiedCommandEnd = false
	err := commandHandler.HandleCommand(runCtx, currentCommand)

	if !runCtx.notifiedCommandEnd {
		if err == nil {
			runner.NotifyCommandOk(runCtx, nil)
		} else {
			runner.onStepEndError(runCtx, err)
		}
	}
}

func (runner *ScriptJobRunner) onScriptEnd(runCtx *ScriptRunContext) {
	log.Infof("%s onScriptEnd", runCtx.GetTag())

	if !runCtx.FinallyNodeReached {
		panic("finallyNodeReached should be true")
	}

	runSummary := *runCtx.RunSummary

	runSummary.Status = runCtx.JobStatus
	runSummary.Detail = runCtx.Detail
	runSummary.UpdateTime = time.Now()

	runCtx.RunSummary = &runSummary

	for _, scriptEndCallback := range runCtx.ScriptEndCallbacks {
		scriptEndCallback(runCtx)
	}

	runCtx.ScriptEnd = true
}

func (runner *ScriptJobRunner) onStepEndError(runCtx *ScriptRunContext, err error) {
	currentScriptNode := runCtx.CurrentScriptNode
	currentCommandIdx := runCtx.currentCommandIdx
	currentCommand := currentScriptNode.CommandList[currentCommandIdx]

	// 缺省只warn因为有可能有catchpoint能catch
	if runCtx.IsWarnEnabled() {
		log.Warnf("%s onStepEndError met err %s, commandBody: %s",
			runCtx.LogPrefix, err, currentScriptNode.CommandDisplayList[currentCommandIdx])
	}

	if currentCommand.GetCatchPointIndex() < 0 {
		runCtx.CurrentScriptNode = nil

		if runCtx.JobStatus != Status_Failed {
			runCtx.JobStatus = Status_Failed
			runCtx.Detail = "met error but no catch point"
			runCtx.FailCause = err
		}

		if runCtx.IsWarnEnabled() {
			log.Errorf("%s onStepEndError met err no catch point, job failed",
				runCtx.LogPrefix)
		} else {
			log.Errorf("%s onStepEndError met err no catch point, job failed, err %s, commandBody: %s",
				runCtx.LogPrefix, err, currentScriptNode.CommandDisplayList[currentCommandIdx])
		}

		if !runCtx.FinallyNodeReached {
			runner.initCurrentScriptNodeForFinally(runCtx)

			if runCtx.CurrentScriptNode == nil {
				runner.onScriptEnd(runCtx)
			}
		} else {
			runner.onScriptEnd(runCtx)
		}

		return
	}

	for len(runCtx.BlockContextList) > 0 {
		lastBlockContext := runCtx.BlockContextList[len(runCtx.BlockContextList)-1]

		if lastBlockContext.GetStartIdx() > currentCommand.GetCatchPointIndex() {
			runCtx.BlockContextList = runCtx.BlockContextList[0 : len(runCtx.BlockContextList)-1]
		} else {
			break
		}
	}

	runCtx.currentCommandIdx = currentCommand.GetCatchPointIndex()
}

func (runner *ScriptJobRunner) onStepEndOk(runCtx *ScriptRunContext) {
	currentScriptNode := runCtx.CurrentScriptNode

	commandList := currentScriptNode.CommandList
	currentCommandIdx := runCtx.currentCommandIdx

	if currentCommandIdx < (len(commandList) - 1) {
		runCtx.currentCommandIdx += 1
		return
	}

	runner.updateCurrentScriptNode(runCtx)

	if runCtx.CurrentScriptNode == nil {
		if runCtx.FinallyNodeReached {
			runner.onScriptEnd(runCtx)
			return
		}

		runCtx.JobStatus = Status_Done
		runCtx.Detail = "run to end"
		runner.initCurrentScriptNodeForFinally(runCtx)

		if runCtx.CurrentScriptNode == nil {
			runner.onScriptEnd(runCtx)
			return
		}
	}
}

// 更新currentScriptNode
func (runner *ScriptJobRunner) updateCurrentScriptNode(runCtx *ScriptRunContext) {
	currentScriptNode := runCtx.CurrentScriptNode
	runCtx.CurrentScriptNode = nil
	parent := currentScriptNode.parent

	for parent != nil {
		nodeList := parent.NodeList

		nodeIdx := slices.Index(nodeList, currentScriptNode)

		if nodeIdx < len(nodeList)-1 {
			currentScriptNode = nodeList[nodeIdx+1]
			runner.initCurrentScriptNode(runCtx, currentScriptNode)
			return
		}

		currentScriptNode = parent
		parent = currentScriptNode.parent
	}
}

func (runner *ScriptJobRunner) initCurrentScriptNodeForFinally(runCtx *ScriptRunContext) {
	rootScriptNode := runCtx.rootScriptNode
	runCtx.FinallyNodeReached = true

	if !rootScriptNode.HasFinallyNode() {
		if runCtx.IsInfoEnabled() {
			log.Infof("%s initCurrentScriptNodeForFinally no finally node", runCtx.GetTag())
		}
		return
	}

	runCtx.RootCtx = runCtx.finallyRootCtx
	runner.initCurrentScriptNode(runCtx, rootScriptNode.FinallyNode)

	if runCtx.IsInfoEnabled() {
		log.Infof("%s initCurrentScriptNodeForFinally start processing finally node", runCtx.GetTag())
	}
}

func (runner *ScriptJobRunner) initCurrentScriptNode(runCtx *ScriptRunContext, scriptNode *ScriptNode) {
	if scriptNode.HasCommands() {
		if runCtx.IsInfoEnabled() {
			log.Infof("%s initCurrentScriptNode: node %s", runCtx.GetTag(), scriptNode.Path)
		}

		runCtx.CurrentScriptNode = scriptNode
		runCtx.currentCommandIdx = 0
		return
	}

	if scriptNode.HasNodes() {
		subScriptNode := scriptNode.NodeList[0]
		runner.initCurrentScriptNode(runCtx, subScriptNode)
		return
	}

	panic("shouldn't come here")
}
