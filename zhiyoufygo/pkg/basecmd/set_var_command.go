package basecmd

import (
	"errors"
	"fmt"
	"slices"
)

const SetVarCommandId = BaseCommandNamespace + "/SetVarCommand"

type SetVarCommand struct {
	BeCatchPoint bool   `json:"isCatchPoint"`
	VarPath      string `json:"varPath"`
	ValueSource  string `json:"valueSource"`
	VarValue     any    `json:"varValue"`
	JsonUrl      string `json:"jsonUrl"`

	catchPointIndex int
}

func (cmd SetVarCommand) GetCommandId() string {
	return SetVarCommandId
}

func (cmd SetVarCommand) GetCatchPointIndex() int {
	return cmd.catchPointIndex
}

func (cmd *SetVarCommand) SetCatchPointIndex(index int) {
	cmd.catchPointIndex = index
}

func (cmd SetVarCommand) IsCatchPoint() bool {
	return cmd.BeCatchPoint
}

func (cmd SetVarCommand) Check() error {
	if cmd.VarPath == "" {
		return errors.New("varPath is empty")
	}

	valueSourceSet := []string{"varValue", "jsonUrl"}

	if cmd.ValueSource != "" && !slices.Contains(valueSourceSet, cmd.ValueSource) {
		return fmt.Errorf("invalid value source %s", cmd.ValueSource)
	}

	if cmd.valueSourceJsonUrl() {
		if cmd.JsonUrl == "" {
			return errors.New("jsonUrl is empty")
		}
	}

	return nil
}

func (cmd SetVarCommand) valueSourceVarValue() bool {
	if cmd.ValueSource == "" || cmd.ValueSource == "varValue" {
		return true
	}

	return false
}

func (cmd SetVarCommand) valueSourceJsonUrl() bool {
	return cmd.ValueSource == "jsonUrl"
}
