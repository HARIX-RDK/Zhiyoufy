package testutils

import (
	"context"
	"fmt"
	"io/fs"
	"net/http"
	"testing"
	"time"

	"github.com/sirupsen/logrus"

	"github.com/zhiyoufy/zhiyoufygo/pkg/core"
	"github.com/zhiyoufy/zhiyoufygo/pkg/script"
	"github.com/zhiyoufy/zhiyoufygo/pkg/util/scriptutil"

	"github.com/stretchr/testify/require"
	"github.com/zhiyoufy/zhiyoufygo/test"
)

type RunnerCustomizer func(*script.ScriptJobRunner) error
type RunContextCustomizer func(*script.ScriptRunContext)

type ScriptTestConfig struct {
	FS                fs.ReadFileFS
	ScriptFile        string
	RunnerCustomizers []RunnerCustomizer
	CtxCustomizers    []RunContextCustomizer
}

func NewHttpClient() *http.Client {
	c := http.Client{Timeout: time.Duration(20) * time.Second}
	return &c
}

func NewScriptRunContext(t *testing.T, runRootCtx context.Context,
	scriptRootNode *script.ScriptRootNode) *script.ScriptRunContext {
	client := NewHttpClient()
	runCtx := script.NewScriptRunContext(script.ScriptRunContextConfig{
		RunId:          t.Name(),
		RootScriptNode: scriptRootNode,

		RootCtx:    runRootCtx,
		HttpClient: client,
	})

	return runCtx
}

func NewCompositeRunContext(t *testing.T, runRootCtx context.Context) *core.CompositeRunContext {
	runContext := &core.CompositeRunContext{
		ILogContext: &core.SimpleLogContext{
			Tag:               t.Name(),
			LogLevelThreshold: logrus.InfoLevel,
		},
	}
	runContext.FillDefault()

	if runRootCtx != nil {
		simpleBaseRunContext := runContext.IBaseRunContext.(*core.SimpleBaseRunContext)
		simpleBaseRunContext.Ctx = runRootCtx
	}

	return runContext
}

func BaseRunScript(t *testing.T, cfg ScriptTestConfig) (*script.ScriptJobRunner, *script.ScriptRunContext) {
	test.LoadConfig(t)

	content, err := cfg.FS.ReadFile(cfg.ScriptFile)

	require.True(t, err == nil, fmt.Sprintf("read script %s failed, %s", cfg.ScriptFile, err))

	runner := scriptutil.NewScriptJobRunner()

	for _, runnerCustomizer := range cfg.RunnerCustomizers {
		err = runnerCustomizer(runner)
		require.True(t, err == nil)
	}

	scriptRootNode, err := runner.ParseScript(string(content))

	require.True(t, err == nil, fmt.Sprintf("parse script %s failed, %s", cfg.ScriptFile, err))

	require.True(t, scriptRootNode != nil)

	runCtx := NewScriptRunContext(t, context.Background(), scriptRootNode)

	for _, ctxCustomizer := range cfg.CtxCustomizers {
		ctxCustomizer(runCtx)
	}

	runner.RunScript(runCtx)

	return runner, runCtx
}

func BaseRunScriptOk(t *testing.T, cfg ScriptTestConfig) (*script.ScriptJobRunner, *script.ScriptRunContext) {
	runner, runCtx := BaseRunScript(t, cfg)

	require.True(t, runCtx.JobStatus == script.Status_Done)

	require.True(t, len(runCtx.BlockContextList) == 0)

	return runner, runCtx
}
