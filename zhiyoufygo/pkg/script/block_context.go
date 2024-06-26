package script

type BlockContexter interface {
	GetBlockName() string
	GetCommandId() string
	GetStartIdx() int
	GetEndIdx() int
}

type BlockContext struct {
	BlockName string
	StartIdx  int
	EndIdx    int
	CommandId string
}

func (ctx BlockContext) GetBlockName() string {
	return ctx.BlockName
}

func (ctx BlockContext) GetCommandId() string {
	return ctx.CommandId
}

func (ctx BlockContext) GetStartIdx() int {
	return ctx.StartIdx
}

func (ctx BlockContext) GetEndIdx() int {
	return ctx.EndIdx
}
