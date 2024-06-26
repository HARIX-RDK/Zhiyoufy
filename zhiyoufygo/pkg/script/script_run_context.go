package script

import (
	"context"
	"fmt"
	"net/http"
	"sync/atomic"

	"github.com/sirupsen/logrus"

	"github.com/zhiyoufy/zhiyoufygo/pkg/core"
	"github.com/zhiyoufy/zhiyoufygo/pkg/core/bus"
)

type ScriptJobStatus string

const (
	Status_Running  ScriptJobStatus = "Running"
	Status_Stopping ScriptJobStatus = "Stopping"
	Status_Done     ScriptJobStatus = "Done"
	Status_Failed   ScriptJobStatus = "Failed"
	Status_Stopped  ScriptJobStatus = "Stopped"
)

func (status ScriptJobStatus) isTerminated() bool {
	return status == Status_Done || status == Status_Failed || status == Status_Stopped
}

type ScriptEndCallback func(core.IRunContext) error

type ScriptRunContext struct {
	bus.Bus
	core.IConfigContext
	core.ILogContext
	core.IVarContext

	RunnerIdx          int
	ChildIdx           int
	RootCtx            context.Context
	HttpClient         *http.Client
	Runner             *ScriptJobRunner
	RunId              string
	LogPrefix          string
	ScriptEnd          bool
	StopRequested      atomic.Bool
	FinallyNodeReached bool
	JobStatus          ScriptJobStatus
	Detail             string
	FailCause          error
	RunSummary         *ScriptRunSummary
	ScriptEndCallbacks []ScriptEndCallback
	InternalVarContext core.IVarContext

	notifiedCommandEnd bool

	finallyRootCtx    context.Context
	rootScriptNode    *ScriptRootNode
	CurrentScriptNode *ScriptNode
	currentCommandIdx int
	currentCommandSeq int
	maxCommandSeq     int
	BlockContextList  []BlockContexter
}

type ScriptRunContextConfig struct {
	bus.Bus
	core.IConfigContext
	core.ILogContext
	core.IVarContext

	RunnerIdx      int
	ChildIdx       int
	RunId          string
	RootScriptNode *ScriptRootNode

	RootCtx        context.Context
	FinallyRootCtx context.Context
	HttpClient     *http.Client
}

func NewScriptRunContext(cfg ScriptRunContextConfig) *ScriptRunContext {
	ctx := &ScriptRunContext{
		RunnerIdx:      cfg.RunnerIdx,
		ChildIdx:       cfg.ChildIdx,
		Bus:            cfg.Bus,
		IConfigContext: cfg.IConfigContext,
		ILogContext:    cfg.ILogContext,
		IVarContext:    cfg.IVarContext,
		RootCtx:        cfg.RootCtx,
		finallyRootCtx: cfg.FinallyRootCtx,
		HttpClient:     cfg.HttpClient,
		RunId:          cfg.RunId,
		rootScriptNode: cfg.RootScriptNode,
	}

	if ctx.Bus == nil {
		ctx.Bus = bus.New()
	}

	if ctx.IConfigContext == nil {
		ctx.IConfigContext = core.NewSimpleConfigContext(nil)
	}

	if ctx.ILogContext == nil {
		ctx.ILogContext = &core.SimpleLogContext{
			Tag:               cfg.RunId,
			LogLevelThreshold: logrus.InfoLevel,
		}
	}

	if ctx.IVarContext == nil {
		ctx.IVarContext = core.NewSimpleVarContext()
	}
	ctx.InternalVarContext = core.NewSimpleVarContext()

	if ctx.finallyRootCtx == nil {
		ctx.finallyRootCtx = context.Background()
	}

	return ctx
}

func (ctx *ScriptRunContext) GetRootContext() context.Context {
	return ctx.RootCtx
}

func (ctx *ScriptRunContext) GetHttpClient() *http.Client {
	return ctx.HttpClient
}

func (ctx *ScriptRunContext) AddScriptEndCallback(callback ScriptEndCallback) {
	ctx.ScriptEndCallbacks = append(ctx.ScriptEndCallbacks, callback)
}

func (ctx *ScriptRunContext) GetCurrentCommandId() string {
	return fmt.Sprintf("%s/%s/%d", ctx.CurrentScriptNode.Path, ctx.CurrentScriptNode.Name, ctx.currentCommandIdx)
}

func (ctx *ScriptRunContext) GetCurrentCommandIdx() int {
	return ctx.currentCommandIdx
}

func (ctx *ScriptRunContext) GetLastBlockContext() BlockContexter {
	var lastBlockContext BlockContexter

	if len(ctx.BlockContextList) > 0 {
		lastBlockContext = ctx.BlockContextList[len(ctx.BlockContextList)-1]
	}

	return lastBlockContext
}
