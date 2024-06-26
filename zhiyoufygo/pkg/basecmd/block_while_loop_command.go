package basecmd

import (
	"errors"
	"fmt"
	"slices"
)

const BlockWhileLoopCommandId = BaseCommandNamespace + "/BlockWhileLoopCommand"

type BlockWhileLoopCommand struct {
	BeCatchPoint bool   `json:"isCatchPoint"`
	BlockName    string `json:"blockName"`

	VarPath   string `json:"varPath"`
	RunIfTrue *bool  `json:"runIfTrue"`

	Operator      string `json:"operator"`
	VarType       string `json:"varType"`
	TargetVarPath string `json:"targetVarPath"`

	catchPointIndex int
}

func (cmd BlockWhileLoopCommand) GetBlockName() string {
	return cmd.BlockName
}

func (cmd BlockWhileLoopCommand) IsBlockBegin() bool {
	return true
}

func (cmd BlockWhileLoopCommand) IsBlockEnd() bool {
	return false
}

func (cmd BlockWhileLoopCommand) GetCommandId() string {
	return BlockWhileLoopCommandId
}

func (cmd BlockWhileLoopCommand) GetCatchPointIndex() int {
	return cmd.catchPointIndex
}

func (cmd *BlockWhileLoopCommand) SetCatchPointIndex(index int) {
	cmd.catchPointIndex = index
}

func (cmd BlockWhileLoopCommand) IsCatchPoint() bool {
	return cmd.BeCatchPoint
}

func (cmd BlockWhileLoopCommand) Check() error {
	if cmd.BlockName == "" {
		return errors.New("BlockName is empty")
	}

	if cmd.VarPath == "" {
		return errors.New("varPath is empty")
	}

	if cmd.Operator != "" {
		if cmd.TargetVarPath == "" {
			return errors.New("targetVarPath is empty")
		}

		varTypeSet := []string{"float64", "string", "bool"}

		if !slices.Contains(varTypeSet, cmd.VarType) {
			return fmt.Errorf("invalid varType %s", cmd.VarType)
		}

		var validOperatorSet []string

		if cmd.VarType == "float64" {
			validOperatorSet = []string{"==", "!=", "<=", ">=", "<", ">"}
		} else {
			validOperatorSet = []string{"==", "!="}
		}

		if !slices.Contains(validOperatorSet, cmd.Operator) {
			return fmt.Errorf("invalid operator %s for type %s", cmd.Operator, cmd.VarType)
		}
	}

	return nil
}
