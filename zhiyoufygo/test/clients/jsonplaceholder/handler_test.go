package jsonplaceholder

import (
	"embed"
	"testing"

	"github.com/zhiyoufy/zhiyoufygo/pkg/handlers/jsonplaceholder"
	"github.com/zhiyoufy/zhiyoufygo/pkg/script"
	"github.com/zhiyoufy/zhiyoufygo/test/testutils"
)

//go:embed testdata
var TestdataFS embed.FS

var runnerCustomizerRegister testutils.RunnerCustomizer = func(runner *script.ScriptJobRunner) error {
	commandHandler := jsonplaceholder.NewCommandHandler()
	err := runner.RegisterCommandHandler(commandHandler)

	return err
}
var runnerCustomizers []testutils.RunnerCustomizer = []testutils.RunnerCustomizer{runnerCustomizerRegister}

func TestRunScriptLoopAllApis(t *testing.T) {
	cfg := testutils.ScriptTestConfig{
		FS:                TestdataFS,
		ScriptFile:        "testdata/loopAllApis.json",
		RunnerCustomizers: runnerCustomizers,
	}

	testutils.BaseRunScriptOk(t, cfg)
}
