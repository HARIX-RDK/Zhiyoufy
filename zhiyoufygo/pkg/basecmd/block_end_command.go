package basecmd

import (
	"errors"
)

const BlockEndCommandId = BaseCommandNamespace + "/BlockEndCommand"

type BlockEndCommand struct {
	BlockName string `json:"blockName"`

	catchPointIndex int
}

func (cmd BlockEndCommand) GetBlockName() string {
	return cmd.BlockName
}

func (cmd BlockEndCommand) IsBlockBegin() bool {
	return false
}

func (cmd BlockEndCommand) IsBlockEnd() bool {
	return true
}

func (cmd BlockEndCommand) GetCommandId() string {
	return BlockEndCommandId
}

func (cmd BlockEndCommand) GetCatchPointIndex() int {
	return cmd.catchPointIndex
}

func (cmd *BlockEndCommand) SetCatchPointIndex(index int) {
	cmd.catchPointIndex = index
}

func (cmd BlockEndCommand) IsCatchPoint() bool {
	return false
}

func (cmd BlockEndCommand) Check() error {
	if cmd.BlockName == "" {
		return errors.New("BlockName is empty")
	}

	return nil
}
