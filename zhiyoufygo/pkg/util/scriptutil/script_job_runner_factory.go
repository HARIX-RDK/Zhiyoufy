package scriptutil

import (
	"github.com/zhiyoufy/zhiyoufygo/pkg/basecmd"
	"github.com/zhiyoufy/zhiyoufygo/pkg/script"
)

func NewScriptJobRunner() *script.ScriptJobRunner {
	runner := script.NewScriptJobRunner()

	baseCommandHandler := basecmd.NewBaseCommandHandler()
	err := runner.RegisterCommandHandler(baseCommandHandler)

	if err != nil {
		panic("failed to register BaseCommandHandler")
	}

	return runner
}
