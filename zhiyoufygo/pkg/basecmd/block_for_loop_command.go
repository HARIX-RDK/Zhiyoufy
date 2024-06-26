package basecmd

import "errors"

const BlockForLoopCommandId = BaseCommandNamespace + "/BlockForLoopCommand"

type ForLoopType string

const (
	ForTypeRangeEnd ForLoopType = "range_end"
	ForTypeInValues ForLoopType = "in_values"
)

type BlockForLoopCommand struct {
	BeCatchPoint bool   `json:"isCatchPoint"`
	BlockName    string `json:"blockName"`

	RangeEnd             *int   `json:"rangeEnd"`
	RangeEndVarPath      string `json:"rangeEndVarPath"`
	InValues             []any  `json:"inValues"`
	InValuesVarPath      string `json:"inValuesVarPath"`
	IterNextValueVarPath string `json:"iterNextValueVarPath"`

	ForType         ForLoopType
	catchPointIndex int
}

func (cmd BlockForLoopCommand) GetBlockName() string {
	return cmd.BlockName
}

func (cmd BlockForLoopCommand) IsBlockBegin() bool {
	return true
}

func (cmd BlockForLoopCommand) IsBlockEnd() bool {
	return false
}

func (cmd BlockForLoopCommand) GetCommandId() string {
	return BlockForLoopCommandId
}

func (cmd BlockForLoopCommand) GetCatchPointIndex() int {
	return cmd.catchPointIndex
}

func (cmd *BlockForLoopCommand) SetCatchPointIndex(index int) {
	cmd.catchPointIndex = index
}

func (cmd BlockForLoopCommand) IsCatchPoint() bool {
	return cmd.BeCatchPoint
}

func (cmd BlockForLoopCommand) Check() error {
	if cmd.BlockName == "" {
		return errors.New("BlockName is empty")
	}

	if (cmd.RangeEnd == nil && cmd.RangeEndVarPath == "") &&
		((cmd.InValues == nil || len(cmd.InValues) == 0) && cmd.InValuesVarPath == "") {
		return errors.New("neither RangeEnd nor InValues specified")
	}

	return nil
}
