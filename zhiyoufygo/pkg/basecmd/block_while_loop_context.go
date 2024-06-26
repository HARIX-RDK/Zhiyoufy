package basecmd

import "github.com/zhiyoufy/zhiyoufygo/pkg/script"

type BlockWhileLoopContext struct {
	script.BlockContext
	StartCommand *BlockWhileLoopCommand
	Condition    bool
}
