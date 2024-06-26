package basecmd

import (
	"errors"
	"fmt"
	"slices"
)

const UpdateVarCommandId = BaseCommandNamespace + "/UpdateVarCommand"

type UpdateVarCommand struct {
	BeCatchPoint bool   `json:"isCatchPoint"`
	VarPath      string `json:"varPath"`
	VarType      string `json:"varType"`
	Operator     string `json:"operator"`
	Operand      any    `json:"operand"`

	catchPointIndex int
}

func (cmd UpdateVarCommand) GetCommandId() string {
	return UpdateVarCommandId
}

func (cmd UpdateVarCommand) GetCatchPointIndex() int {
	return cmd.catchPointIndex
}

func (cmd *UpdateVarCommand) SetCatchPointIndex(index int) {
	cmd.catchPointIndex = index
}

func (cmd UpdateVarCommand) IsCatchPoint() bool {
	return cmd.BeCatchPoint
}

func (cmd UpdateVarCommand) Check() error {
	if cmd.VarPath == "" {
		return errors.New("varPath is empty")
	}

	varTypeSet := []string{"float64", "bool"}

	if !slices.Contains(varTypeSet, cmd.VarType) {
		return fmt.Errorf("invalid varType %s", cmd.VarType)
	}

	var validOperatorSet []string

	switch cmd.VarType {
	case "float64":
		validOperatorSet = []string{"=", "+", "-", "*", "/"}
	case "bool":
		validOperatorSet = []string{"!"}
	}

	if !slices.Contains(validOperatorSet, cmd.Operator) {
		return fmt.Errorf("invalid operator %s for type %s", cmd.Operator, cmd.VarType)
	}

	return nil
}
