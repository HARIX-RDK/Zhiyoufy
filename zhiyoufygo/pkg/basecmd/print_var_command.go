package basecmd

import (
	"errors"
)

const PrintVarCommandId = BaseCommandNamespace + "/PrintVarCommand"

type PrintVarCommand struct {
	BeCatchPoint bool   `json:"isCatchPoint"`
	VarPath      string `json:"varPath"`

	catchPointIndex int
}

func (cmd PrintVarCommand) GetCommandId() string {
	return PrintVarCommandId
}

func (cmd PrintVarCommand) GetCatchPointIndex() int {
	return cmd.catchPointIndex
}

func (cmd *PrintVarCommand) SetCatchPointIndex(index int) {
	cmd.catchPointIndex = index
}

func (cmd PrintVarCommand) IsCatchPoint() bool {
	return cmd.BeCatchPoint
}

func (cmd PrintVarCommand) Check() error {
	if cmd.VarPath == "" {
		return errors.New("varPath is empty")
	}

	return nil
}
