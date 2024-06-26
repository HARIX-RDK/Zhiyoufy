package basecmd

import (
	"encoding/json"
	"fmt"
	"io"
	"math/rand"
	"net/http"
	"time"

	"github.com/mitchellh/mapstructure"
	log "github.com/sirupsen/logrus"

	"github.com/zhiyoufy/zhiyoufygo/pkg/filemgr"
	"github.com/zhiyoufy/zhiyoufygo/pkg/loader/fileloader"
	"github.com/zhiyoufy/zhiyoufygo/pkg/script"
	"github.com/zhiyoufy/zhiyoufygo/pkg/util/convutil"
)

type BaseCommandHandler struct {
}

func NewBaseCommandHandler() *BaseCommandHandler {
	handler := BaseCommandHandler{}
	return &handler
}

func (handler BaseCommandHandler) GetCommandNamespace() string {
	return BaseCommandNamespace
}

func (handler BaseCommandHandler) ParseCommand(commandId string, data []byte) (script.ICommand, error) {
	var icmd script.ICommand
	var err error

	switch commandId {
	case BlockEndCommandId:
		cmd := BlockEndCommand{}
		icmd = &cmd
		err = json.Unmarshal(data, &cmd)

		if err != nil {
			return nil, err
		}
	case BlockForLoopCommandId:
		cmd := BlockForLoopCommand{}
		icmd = &cmd
		err = json.Unmarshal(data, &cmd)

		if err != nil {
			return nil, err
		}
	case BlockIfConditionCommandId:
		cmd := BlockIfConditionCommand{}
		icmd = &cmd
		err = json.Unmarshal(data, &cmd)

		if err != nil {
			return nil, err
		}
	case BlockWhileLoopCommandId:
		cmd := BlockWhileLoopCommand{}
		icmd = &cmd
		err = json.Unmarshal(data, &cmd)

		if err != nil {
			return nil, err
		}
	case DownloadFileCommandId:
		cmd := DownloadFileCommand{}
		icmd = &cmd
		err = json.Unmarshal(data, &cmd)

		if err != nil {
			return nil, err
		}
	case DownloadFilesCommandId:
		cmd := DownloadFilesCommand{}
		icmd = &cmd
		err = json.Unmarshal(data, &cmd)

		if err != nil {
			return nil, err
		}
	case LoadFileCommandId:
		cmd := LoadFileCommand{}
		icmd = &cmd
		err = json.Unmarshal(data, &cmd)

		if err != nil {
			return nil, err
		}
	case PrintVarCommandId:
		cmd := PrintVarCommand{}
		icmd = &cmd
		err = json.Unmarshal(data, &cmd)

		if err != nil {
			return nil, err
		}
	case SetVarCommandId:
		cmd := SetVarCommand{}
		icmd = &cmd
		err = json.Unmarshal(data, &cmd)

		if err != nil {
			return nil, err
		}
	case SetVarFromCollectionCommandId:
		cmd := SetVarFromCollectionCommand{}
		icmd = &cmd
		err = json.Unmarshal(data, &cmd)

		if err != nil {
			return nil, err
		}
	case SetVarFromConfigCommandId:
		cmd := SetVarFromConfigCommand{}
		icmd = &cmd
		err = json.Unmarshal(data, &cmd)

		if err != nil {
			return nil, err
		}
	case SleepCommandId:
		cmd := SleepCommand{}
		icmd = &cmd
		err = json.Unmarshal(data, &cmd)

		if err != nil {
			return nil, err
		}
	case TriggerErrorCommandId:
		cmd := TriggerErrorCommand{
			ErrorProbability: 1,
		}
		icmd = &cmd
		err = json.Unmarshal(data, &cmd)

		if err != nil {
			return nil, err
		}
	case UpdateVarCommandId:
		cmd := UpdateVarCommand{}
		icmd = &cmd
		err = json.Unmarshal(data, &cmd)

		if err != nil {
			return nil, err
		}
	default:
		return nil, fmt.Errorf("invalid commandId %s", commandId)
	}

	if err = icmd.Check(); err != nil {
		return nil, err
	}

	return icmd, nil
}

func (handler BaseCommandHandler) HandleCommand(runCtx *script.ScriptRunContext, icmd script.ICommand) error {
	switch cmd := icmd.(type) {
	case *BlockEndCommand:
		return handler.handleBlockEndCommand(runCtx, cmd)
	case *BlockForLoopCommand:
		return handler.handleBlockForLoopCommand(runCtx, cmd)
	case *BlockIfConditionCommand:
		return handler.handleBlockIfConditionCommand(runCtx, cmd)
	case *BlockWhileLoopCommand:
		return handler.handleBlockWhileLoopCommand(runCtx, cmd)
	case *DownloadFileCommand:
		return handler.handleDownloadFileCommand(runCtx, cmd)
	case *DownloadFilesCommand:
		return handler.handleDownloadFilesCommand(runCtx, cmd)
	case *LoadFileCommand:
		return handler.handleLoadFileCommand(runCtx, cmd)
	case *PrintVarCommand:
		return handler.handlePrintVarCommand(runCtx, cmd)
	case *SetVarCommand:
		return handler.handleSetVarCommand(runCtx, cmd)
	case *SetVarFromCollectionCommand:
		return handler.handleSetVarFromCollectionCommand(runCtx, cmd)
	case *SetVarFromConfigCommand:
		return handler.handleSetVarFromConfigCommand(runCtx, cmd)
	case *SleepCommand:
		return handler.handleSleepCommand(runCtx, cmd)
	case *TriggerErrorCommand:
		return handler.handleTriggerErrorCommand(runCtx, cmd)
	case *UpdateVarCommand:
		return handler.handleUpdateVarCommand(runCtx, cmd)
	}

	return fmt.Errorf("%s not handled", runCtx.LogPrefix)
}

func (handler BaseCommandHandler) handleBlockEndCommand(runCtx *script.ScriptRunContext, cmd *BlockEndCommand) error {
	blockContext := runCtx.GetLastBlockContext()

	switch blockContext.GetCommandId() {
	case BlockForLoopCommandId:
		loopCtx := blockContext.(*BlockForLoopContext)
		return handler.handleBlockForLoopCommandEnd(runCtx, loopCtx)
	case BlockIfConditionCommandId:
		return handler.handleBlockIfConditionCommandEnd(runCtx)
	case BlockWhileLoopCommandId:
		loopCtx := blockContext.(*BlockWhileLoopContext)
		return handler.handleBlockWhileLoopCommandEnd(runCtx, loopCtx)
	}
	return nil
}

func (handler BaseCommandHandler) handleBlockForLoopCommand(runCtx *script.ScriptRunContext, cmd *BlockForLoopCommand) error {
	lastBlockContext := runCtx.GetLastBlockContext()

	if lastBlockContext == nil || lastBlockContext.GetStartIdx() != runCtx.GetCurrentCommandIdx() {

		if cmd.RangeEnd != nil || cmd.RangeEndVarPath != "" {
			if cmd.RangeEnd == nil {
				rangeEnd, ok := runCtx.GetVar(cmd.RangeEndVarPath)

				if !ok {
					return fmt.Errorf("varPath %s not exist", cmd.RangeEndVarPath)
				}

				intRangeEnd, err := convutil.ToInt(rangeEnd)
				if err != nil {
					return err
				}

				cmd.RangeEnd = &intRangeEnd
			}
			cmd.ForType = ForTypeRangeEnd
		} else {
			if cmd.InValues == nil || len(cmd.InValues) == 0 {
				inValues, ok := runCtx.GetVar(cmd.InValuesVarPath)
				if !ok {
					return fmt.Errorf("varPath %s not exist", cmd.InValuesVarPath)
				}

				cmd.InValues = inValues.([]any)
			}
			cmd.ForType = ForTypeInValues
		}
		currentScriptNode := runCtx.CurrentScriptNode
		blockContextBase := currentScriptNode.BlockContextMap[cmd.BlockName]

		blockForLoopContext := new(BlockForLoopContext)
		blockForLoopContext.BlockContext = *blockContextBase
		blockForLoopContext.StartCommand = cmd
		blockForLoopContext.Idx = 0

		runCtx.BlockContextList = append(runCtx.BlockContextList, blockForLoopContext)
	}

	lastBlockContext = runCtx.GetLastBlockContext()
	blockForLoopContext := lastBlockContext.(*BlockForLoopContext)

	var iterNextValue any
	if cmd.ForType == ForTypeRangeEnd {
		iterNextValue = blockForLoopContext.Idx

	} else {
		iterNextValue = cmd.InValues[blockForLoopContext.Idx]
	}

	blockForLoopContext.IterNextValue = iterNextValue
	if cmd.IterNextValueVarPath != "" {
		runCtx.SetVar(cmd.IterNextValueVarPath, iterNextValue)
	}
	return nil
}

func (handler BaseCommandHandler) handleBlockForLoopCommandEnd(runCtx *script.ScriptRunContext, loopCtx *BlockForLoopContext) error {

	if loopCtx.StartCommand.ForType == ForTypeRangeEnd {
		if runCtx.IsInfoEnabled() {
			log.Infof("%s Idx %d, RangeEnd %d", runCtx.GetTag(), loopCtx.Idx, *loopCtx.StartCommand.RangeEnd)
		}
		if loopCtx.Idx < (*loopCtx.StartCommand.RangeEnd - 1) {
			loopCtx.Idx += 1

			nextHopInfo := script.NextHopInfo{
				NextCommandIdx: loopCtx.GetStartIdx(),
			}

			runCtx.Runner.NotifyCommandOk(runCtx, &nextHopInfo)
			return nil
		}

	}
	if loopCtx.StartCommand.ForType == ForTypeInValues {
		if runCtx.IsInfoEnabled() {
			log.Infof("%s Idx %d, loopCount %d", runCtx.GetTag(), loopCtx.Idx, len(loopCtx.StartCommand.InValues))
		}
		if loopCtx.Idx < (len(loopCtx.StartCommand.InValues) - 1) {
			loopCtx.Idx += 1

			nextHopInfo := script.NextHopInfo{
				NextCommandIdx: loopCtx.GetStartIdx(),
			}

			runCtx.Runner.NotifyCommandOk(runCtx, &nextHopInfo)
			return nil
		}

	}

	runCtx.BlockContextList = runCtx.BlockContextList[:len(runCtx.BlockContextList)-1]
	return nil
}

func (handler BaseCommandHandler) handleBlockIfConditionCommand(runCtx *script.ScriptRunContext, cmd *BlockIfConditionCommand) error {
	var condition bool
	var err error

	if cmd.Operator == "" {
		condition, err = runCtx.GetVarTypeBool(cmd.VarPath)

		if err != nil {
			return err
		}
	} else {
		condition, err = handler.getValueAndBoolEvaluate(runCtx, cmd.Operator, cmd.VarType,
			cmd.VarPath, cmd.TargetVarPath)

		if err != nil {
			return err
		}
	}

	if cmd.RunIfTrue != nil && !*cmd.RunIfTrue {
		condition = !condition
	}

	if runCtx.IsInfoEnabled() {
		log.Infof("%s condition %v", runCtx.GetTag(), condition)
	}

	currentScriptNode := runCtx.CurrentScriptNode
	blockContextBase := currentScriptNode.BlockContextMap[cmd.BlockName]

	blockContext := *blockContextBase

	runCtx.BlockContextList = append(runCtx.BlockContextList, &blockContext)

	if condition {
		return nil
	}

	nextHopInfo := script.NextHopInfo{
		NextCommandIdx: blockContext.GetEndIdx(),
	}

	runCtx.Runner.NotifyCommandOk(runCtx, &nextHopInfo)

	return nil
}

func (handler BaseCommandHandler) handleBlockIfConditionCommandEnd(runCtx *script.ScriptRunContext) error {
	runCtx.BlockContextList = runCtx.BlockContextList[:len(runCtx.BlockContextList)-1]
	return nil
}

func (handler BaseCommandHandler) handleBlockWhileLoopCommand(runCtx *script.ScriptRunContext, cmd *BlockWhileLoopCommand) error {
	var condition bool
	var err error

	if cmd.Operator == "" {
		condition, err = runCtx.GetVarTypeBool(cmd.VarPath)

		if err != nil {
			return err
		}
	} else {
		condition, err = handler.getValueAndBoolEvaluate(runCtx, cmd.Operator, cmd.VarType,
			cmd.VarPath, cmd.TargetVarPath)

		if err != nil {
			return err
		}
	}

	if cmd.RunIfTrue != nil && !*cmd.RunIfTrue {
		condition = !condition
	}

	if runCtx.IsInfoEnabled() {
		log.Infof("%s condition %v", runCtx.GetTag(), condition)
	}

	lastBlockContext := runCtx.GetLastBlockContext()

	if lastBlockContext == nil || lastBlockContext.GetStartIdx() != runCtx.GetCurrentCommandIdx() {
		currentScriptNode := runCtx.CurrentScriptNode
		blockContextBase := currentScriptNode.BlockContextMap[cmd.BlockName]

		blockWhileLoopContext := new(BlockWhileLoopContext)
		blockWhileLoopContext.BlockContext = *blockContextBase
		blockWhileLoopContext.StartCommand = cmd
		blockWhileLoopContext.Condition = condition

		runCtx.BlockContextList = append(runCtx.BlockContextList, blockWhileLoopContext)

		lastBlockContext = blockWhileLoopContext
	} else {
		blockWhileLoopContext := lastBlockContext.(*BlockWhileLoopContext)
		blockWhileLoopContext.Condition = condition
	}

	if condition {
		return nil
	}

	nextHopInfo := script.NextHopInfo{
		NextCommandIdx: lastBlockContext.GetEndIdx(),
	}

	runCtx.Runner.NotifyCommandOk(runCtx, &nextHopInfo)

	return nil
}

func (handler BaseCommandHandler) handleBlockWhileLoopCommandEnd(runCtx *script.ScriptRunContext, loopCtx *BlockWhileLoopContext) error {
	if runCtx.IsInfoEnabled() {
		log.Infof("%s condition %v", runCtx.GetTag(), loopCtx.Condition)
	}

	if loopCtx.Condition {
		nextHopInfo := script.NextHopInfo{
			NextCommandIdx: loopCtx.GetStartIdx(),
		}

		runCtx.Runner.NotifyCommandOk(runCtx, &nextHopInfo)

		return nil
	}

	runCtx.BlockContextList = runCtx.BlockContextList[:len(runCtx.BlockContextList)-1]
	return nil
}

func (handler BaseCommandHandler) handleDownloadFileCommand(runCtx *script.ScriptRunContext, cmd *DownloadFileCommand) error {
	cmdId := runCtx.GetCurrentCommandId()

	var cmdState *DownloadFileCommandState

	if cmd.DataVarPath != "" {
		cmdState = &DownloadFileCommandState{
			Data: &DownloadFileData{},
		}

		data, ok := runCtx.GetVar(cmd.DataVarPath)
		if !ok {
			return fmt.Errorf("no var exist in path %s", cmd.DataVarPath)
		}
		err := mapstructure.Decode(data, cmdState.Data)
		if err != nil {
			return err
		}

	} else {
		cmdStateRaw, ok := runCtx.InternalVarContext.GetVar(cmdId)
		if !ok {
			cmdState = &DownloadFileCommandState{
				Data: &DownloadFileData{},
			}
			runCtx.InternalVarContext.SetVar(cmdId, cmdState)

			if cmd.Data != nil {
				*(cmdState.Data) = *(cmd.Data)
			}

			if cmd.DataCfgPath != "" {
				var data any
				var ok bool

				data, ok = runCtx.GetConfig(cmd.DataCfgPath)
				if !ok {
					return fmt.Errorf("no var exist in path %s", cmd.DataCfgPath)
				}

				err := mapstructure.Decode(data, cmdState.Data)
				if err != nil {
					return err
				}
			}
		} else {
			cmdState = cmdStateRaw.(*DownloadFileCommandState)
		}
	}

	data := cmdState.Data
	err := filemgr.DefaultFileMgr.DownloadFileIfNeeded(runCtx, data.FileUrl, data.FileDigest, data.LocalPath)

	return err
}

func (handler BaseCommandHandler) handleDownloadFilesCommand(runCtx *script.ScriptRunContext, cmd *DownloadFilesCommand) error {
	cmdId := runCtx.GetCurrentCommandId()

	var cmdState *DownloadFilesCommandState

	if cmd.DataListVarPath != "" {
		cmdState = &DownloadFilesCommandState{
			DataList: []*DownloadFileData{},
		}

		data, ok := runCtx.GetVar(cmd.DataListVarPath)
		if !ok {
			return fmt.Errorf("no var exist in path %s", cmd.DataListVarPath)
		}
		log.Infof("data=%+v", data)
		err := mapstructure.Decode(data, &cmdState.DataList)
		if err != nil {
			return err
		}

	} else {
		cmdStateRaw, ok := runCtx.InternalVarContext.GetVar(cmdId)
		if !ok {
			cmdState = &DownloadFilesCommandState{
				DataList: []*DownloadFileData{},
			}
			runCtx.InternalVarContext.SetVar(cmdId, cmdState)

			if cmd.DataList != nil {
				cmdState.DataList = cmd.DataList
			}

			if cmd.DataListCfgPath != "" {
				var data any
				var ok bool

				data, ok = runCtx.GetConfig(cmd.DataListCfgPath)
				if !ok {
					return fmt.Errorf("no var exist in path %s", cmd.DataListCfgPath)
				}

				err := mapstructure.Decode(data, &cmdState.DataList)
				if err != nil {
					return err
				}
			}
		} else {
			cmdState = cmdStateRaw.(*DownloadFilesCommandState)
		}
	}

	dataList := cmdState.DataList
	for _, data := range dataList {
		err := filemgr.DefaultFileMgr.DownloadFileIfNeeded(runCtx, data.FileUrl, data.FileDigest, data.LocalPath)
		if err != nil {
			return err
		}
	}

	return nil
}

func (handler BaseCommandHandler) handleLoadFileCommand(runCtx *script.ScriptRunContext, cmd *LoadFileCommand) error {
	cmdId := runCtx.GetCurrentCommandId()
	cmdStateRaw, ok := runCtx.InternalVarContext.GetVar(cmdId)

	var cmdState *LoadFileCommandState

	if !ok {
		cmdState = &LoadFileCommandState{
			Data: &LoadFileData{},
		}
		runCtx.InternalVarContext.SetVar(cmdId, cmdState)

		if cmd.Data != nil {
			*(cmdState.Data) = *(cmd.Data)
		}

		if cmd.DataVarPath != "" || cmd.DataCfgPath != "" {
			var data any
			var ok bool

			if cmd.DataVarPath != "" {
				data, ok = runCtx.GetVar(cmd.DataVarPath)
				if !ok {
					return fmt.Errorf("no var exist in path %s", cmd.DataVarPath)
				}

			} else if cmd.DataCfgPath != "" {
				data, ok = runCtx.GetConfig(cmd.DataCfgPath)
				if !ok {
					return fmt.Errorf("no var exist in path %s", cmd.DataVarPath)
				}
			}

			err := mapstructure.Decode(data, cmdState.Data)
			if err != nil {
				return err
			}
		}
	} else {
		cmdState = cmdStateRaw.(*LoadFileCommandState)
	}

	data := cmdState.Data
	var fileData any
	var err error

	if data.FileType == "" || data.FileType == "[]byte" {
		fileData, err = fileloader.Load(runCtx.GetRootContext(), runCtx, nil, data.LocalPath)
	} else if data.FileType == "[]any" {
		fileData, err = fileloader.LoadJsonArray(runCtx.GetRootContext(), runCtx, nil, data.LocalPath)
	}

	if err != nil {
		return err
	}

	runCtx.SetVar(data.TargetVarPath, fileData)

	return nil
}

func (handler BaseCommandHandler) handlePrintVarCommand(runCtx *script.ScriptRunContext, cmd *PrintVarCommand) error {
	varValue, _ := runCtx.GetVar(cmd.VarPath)

	if runCtx.IsInfoEnabled() {
		log.Infof("%s varPath %s, varValue %v", runCtx.GetTag(),
			cmd.VarPath, varValue)
	}

	return nil
}

func (handler BaseCommandHandler) handleSetVarCommand(runCtx *script.ScriptRunContext, cmd *SetVarCommand) error {
	if cmd.valueSourceVarValue() {
		runCtx.SetVar(cmd.VarPath, cmd.VarValue)
	} else if cmd.valueSourceJsonUrl() {
		req, err := http.NewRequestWithContext(runCtx.RootCtx, http.MethodGet, cmd.JsonUrl, nil)
		if err != nil {
			return err
		}

		rsp, err := runCtx.HttpClient.Do(req)
		if err != nil {
			return err
		}
		defer rsp.Body.Close()

		body, err := io.ReadAll(rsp.Body)
		if err != nil {
			return err
		}

		var varValue any
		err = json.Unmarshal(body, &varValue)
		if err != nil {
			return err
		}

		runCtx.SetVar(cmd.VarPath, varValue)
	}

	return nil
}

func (handler BaseCommandHandler) handleSetVarFromCollectionCommand(runCtx *script.ScriptRunContext, cmd *SetVarFromCollectionCommand) error {
	collectionRaw, ok := runCtx.GetVar(cmd.CollectionVarPath)
	if !ok {
		return fmt.Errorf("collectionVar %s not exist", cmd.CollectionVarPath)
	}

	var target any

	if cmd.CollectionType == "array" {
		collectionArray, ok := collectionRaw.([]any)
		if !ok {
			return fmt.Errorf("collectionVar is not array but %T", collectionRaw)
		}

		arrayIdx := cmd.ArrayIdx
		stride := 0
		var err error
		if cmd.ArrayIdxStrideVarPath != "" {
			stride, err = runCtx.GetVarTypeInt(cmd.ArrayIdxStrideVarPath)
			if err != nil {
				return fmt.Errorf("var %s is not int: %v", cmd.ArrayIdxStrideVarPath, err)
			}
		}

		if cmd.ArrayIdxUseChildIdx {
			arrayIdx = runCtx.ChildIdx + stride*runCtx.RunnerIdx
		}

		if arrayIdx >= len(collectionArray) {
			return fmt.Errorf("arrayIdx %d is out of range (array len %d)", arrayIdx, len(collectionArray))
		}

		target = collectionArray[arrayIdx]
	} else if cmd.CollectionType == "map" {
		collectionMap, ok := collectionRaw.(map[string]any)
		if !ok {
			return fmt.Errorf("collectionVar is not map but %T", collectionMap)
		}
		target, ok = collectionMap[cmd.MapKey]
		if !ok {
			return fmt.Errorf("key %s doesn't exist", cmd.MapKey)
		}
	}

	if runCtx.IsInfoEnabled() {
		log.Infof("%s set varPath %s to %v", runCtx.GetTag(), cmd.VarPath, target)
	}

	runCtx.SetVar(cmd.VarPath, target)

	return nil
}

func (handler BaseCommandHandler) handleSetVarFromConfigCommand(runCtx *script.ScriptRunContext, cmd *SetVarFromConfigCommand) error {
	cfgValue, ok := runCtx.GetConfig(cmd.ConfigPath)
	if !ok {
		return fmt.Errorf("config %s not exist", cmd.ConfigPath)
	}

	if runCtx.IsInfoEnabled() {
		log.Infof("%s set varPath %s to %v", runCtx.GetTag(), cmd.VarPath, cfgValue)
	}

	runCtx.SetVar(cmd.VarPath, cfgValue)

	return nil
}

func (handler BaseCommandHandler) handleSleepCommand(runCtx *script.ScriptRunContext, cmd *SleepCommand) error {
	var timeSeconds float64

	if cmd.TimeSeconds != nil {
		timeSeconds = *cmd.TimeSeconds

	} else {
		timeSeconds = *cmd.TimeSecondsMin + rand.Float64()*(*cmd.TimeSecondsMax-*cmd.TimeSecondsMin)
	}

	if runCtx.IsInfoEnabled() {
		log.Infof("%s timeSeconds %v", runCtx.GetTag(), timeSeconds)
	}

	select {
	case <-runCtx.RootCtx.Done():
	case <-time.After(time.Duration(timeSeconds*1000) * time.Millisecond):
	}

	return nil
}

func (handler BaseCommandHandler) handleTriggerErrorCommand(runCtx *script.ScriptRunContext, cmd *TriggerErrorCommand) error {
	randValue := rand.Float64()

	if cmd.ErrorProbability > randValue {
		return fmt.Errorf("TriggerErrorCommand randValue %.2f, errorProb %.2f",
			randValue, cmd.ErrorProbability)
	}

	return nil
}

func (handler BaseCommandHandler) handleUpdateVarCommand(runCtx *script.ScriptRunContext, cmd *UpdateVarCommand) error {
	orig, ok := runCtx.GetVar(cmd.VarPath)
	if !ok {
		return fmt.Errorf("varPath %s not exist", cmd.VarPath)
	}
	switch cmd.VarType {
	case "float64":
		arg1Float64, err := convutil.ToFloat64(orig)
		if err != nil {
			return fmt.Errorf("varPath %s can't be convert to float64: %w", cmd.VarPath, err)
		}
		arg2Float64, err := convutil.ToFloat64(cmd.Operand)
		if err != nil {
			return fmt.Errorf("operand %v can't be convert to float64: %w", cmd.Operand, err)
		}
		updateValue, err := handler.float64EvaluateFloat64Expr(cmd.Operator, arg1Float64, arg2Float64)
		if err != nil {
			return err
		}

		if runCtx.IsInfoEnabled() {
			log.Infof("%s update varPath %s to %v", runCtx.GetTag(), cmd.VarPath, updateValue)
		}
		runCtx.SetVar(cmd.VarPath, updateValue)
		return nil
	case "bool":
		arg1Bool, ok := orig.(bool)
		if !ok {
			return fmt.Errorf("orig %v can't be convert to bool", orig)
		}

		var arg2Bool bool
		switch cmd.Operator {
		case "!":
		default:
			arg2Bool, ok = cmd.Operand.(bool)
			if !ok {
				return fmt.Errorf("operand %v can't be convert to bool", orig)
			}
		}

		updateValue, err := handler.boolEvaluateBoolExpr(cmd.Operator, arg1Bool, arg2Bool)
		if err != nil {
			return err
		}

		if runCtx.IsInfoEnabled() {
			log.Infof("%s update varPath %s to %v", runCtx.GetTag(), cmd.VarPath, updateValue)
		}
		runCtx.SetVar(cmd.VarPath, updateValue)
		return nil
	default:
		return fmt.Errorf("unsupported varType %s", cmd.VarType)
	}
}

func (handler BaseCommandHandler) getValueAndBoolEvaluate(runCtx *script.ScriptRunContext,
	operator, varType, arg1Path, arg2Path string) (bool, error) {
	arg1, _ := runCtx.GetVar(arg1Path)
	arg2, _ := runCtx.GetVar(arg2Path)

	return handler.boolEvaluate(operator, varType, arg1, arg2)
}

func (handler BaseCommandHandler) boolEvaluate(operator, varType string, arg1, arg2 any) (bool, error) {
	if varType == "bool" {
		arg1Bool, ok := arg1.(bool)
		if !ok {
			return false, fmt.Errorf("arg1 %v is not bool", arg1)
		}
		arg2Bool, ok := arg2.(bool)
		if !ok {
			return false, fmt.Errorf("arg2 %v is not bool", arg2)
		}

		return handler.boolEvaluateBoolExpr(operator, arg1Bool, arg2Bool)
	}

	if varType == "float64" {
		arg1Float64, err := convutil.ToFloat64(arg1)
		if err != nil {
			return false, err
		}
		arg2Float64, err := convutil.ToFloat64(arg2)
		if err != nil {
			return false, err
		}

		return handler.boolEvaluateFloat64Expr(operator, arg1Float64, arg2Float64)
	}

	if varType == "string" {
		arg1String, ok := arg1.(string)
		if !ok {
			return false, fmt.Errorf("arg1 %v is not string", arg1)
		}
		arg2String, ok := arg2.(string)
		if !ok {
			return false, fmt.Errorf("arg2 %v is not string", arg2)
		}

		return handler.boolEvaluateStringExpr(operator, arg1String, arg2String)
	}

	return false, fmt.Errorf("invalid varType %s", varType)
}

func (handler BaseCommandHandler) boolEvaluateBoolExpr(operator string, arg1, arg2 bool) (bool, error) {
	switch operator {
	case "==":
		return arg1 == arg2, nil
	case "!=":
		return arg1 != arg2, nil
	case "!":
		return !arg1, nil
	}

	return false, fmt.Errorf("invalid operator %s", operator)
}

func (handler BaseCommandHandler) boolEvaluateFloat64Expr(operator string, arg1, arg2 float64) (bool, error) {
	switch operator {
	case "==":
		return arg1 == arg2, nil
	case "!=":
		return arg1 != arg2, nil
	case "<":
		return arg1 < arg2, nil
	case "<=":
		return arg1 <= arg2, nil
	case ">":
		return arg1 > arg2, nil
	case ">=":
		return arg1 >= arg2, nil
	}

	return false, fmt.Errorf("invalid operator %s", operator)
}

func (handler BaseCommandHandler) boolEvaluateStringExpr(operator string, arg1, arg2 string) (bool, error) {
	switch operator {
	case "==":
		return arg1 == arg2, nil
	case "!=":
		return arg1 != arg2, nil
	}

	return false, fmt.Errorf("invalid operator %s", operator)
}

func (handler BaseCommandHandler) float64EvaluateFloat64Expr(operator string, arg1, arg2 float64) (float64, error) {
	switch operator {
	case "=":
		return arg2, nil
	case "+":
		return arg1 + arg2, nil
	case "-":
		return arg1 - arg2, nil
	case "*":
		return arg1 * arg2, nil
	case "/":
		return arg1 / arg2, nil
	}

	return 0, fmt.Errorf("invalid operator %s", operator)
}
