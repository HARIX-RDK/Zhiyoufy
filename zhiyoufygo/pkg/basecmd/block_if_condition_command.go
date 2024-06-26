package basecmd

import (
	"errors"
	"fmt"
	"slices"
)

const BlockIfConditionCommandId = BaseCommandNamespace + "/BlockIfConditionCommand"

type BlockIfConditionCommand struct {
	BeCatchPoint bool   `json:"isCatchPoint"`
	BlockName    string `json:"blockName"`

	VarPath   string `json:"varPath"`
	RunIfTrue *bool  `json:"runIfTrue"`

	Operator      string `json:"operator"`
	VarType       string `json:"varType"`
	TargetVarPath string `json:"targetVarPath"`

	catchPointIndex int
}

func (cmd BlockIfConditionCommand) GetBlockName() string {
	return cmd.BlockName
}

func (cmd BlockIfConditionCommand) IsBlockBegin() bool {
	return true
}

func (cmd BlockIfConditionCommand) IsBlockEnd() bool {
	return false
}

func (cmd BlockIfConditionCommand) GetCommandId() string {
	return BlockIfConditionCommandId
}

func (cmd BlockIfConditionCommand) GetCatchPointIndex() int {
	return cmd.catchPointIndex
}

func (cmd *BlockIfConditionCommand) SetCatchPointIndex(index int) {
	cmd.catchPointIndex = index
}

func (cmd BlockIfConditionCommand) IsCatchPoint() bool {
	return cmd.BeCatchPoint
}

func (cmd BlockIfConditionCommand) Check() error {
	if cmd.BlockName == "" {
		return errors.New("blockName is empty")
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
