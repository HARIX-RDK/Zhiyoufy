package script

type IBlockCommand interface {
	ICommand
	GetBlockName() string
	IsBlockBegin() bool
	IsBlockEnd() bool
}

type IBlockCatchGotoEnd interface {
	BlockCatchGotoEnd() bool
}
