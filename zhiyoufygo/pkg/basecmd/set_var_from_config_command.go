package basecmd

import (
	"errors"
)

const SetVarFromConfigCommandId = BaseCommandNamespace + "/SetVarFromConfigCommand"

type SetVarFromConfigCommand struct {
	BeCatchPoint bool   `json:"isCatchPoint"`
	VarPath      string `json:"varPath"`
	ConfigPath   string `json:"configPath"`

	catchPointIndex int
}

func (cmd SetVarFromConfigCommand) GetCommandId() string {
	return SetVarFromConfigCommandId
}

func (cmd SetVarFromConfigCommand) GetCatchPointIndex() int {
	return cmd.catchPointIndex
}

func (cmd *SetVarFromConfigCommand) SetCatchPointIndex(index int) {
	cmd.catchPointIndex = index
}

func (cmd SetVarFromConfigCommand) IsCatchPoint() bool {
	return cmd.BeCatchPoint
}

func (cmd SetVarFromConfigCommand) Check() error {
	if cmd.VarPath == "" {
		return errors.New("varPath is empty")
	}

	if cmd.ConfigPath == "" {
		return errors.New("configPath is empty")
	}

	return nil
}
