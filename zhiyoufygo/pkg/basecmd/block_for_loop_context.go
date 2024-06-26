package basecmd

import "github.com/zhiyoufy/zhiyoufygo/pkg/script"

type BlockForLoopContext struct {
	script.BlockContext
	StartCommand  *BlockForLoopCommand
	Idx           int
	IterNextValue any
}
