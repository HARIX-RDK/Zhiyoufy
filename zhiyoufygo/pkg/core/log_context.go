package core

import (
	"github.com/sirupsen/logrus"
	"github.com/zhiyoufy/zhiyoufygo/pkg/util/randutil"
)

type ILogContext interface {
	GetTag() string
	GetLogLevelThreshold() logrus.Level
	SetLogLevelThreshold(lvl logrus.Level)
	IsTraceEnabled() bool
	IsDebugEnabled() bool
	IsInfoEnabled() bool
	IsWarnEnabled() bool
	IsErrorEnabled() bool
}

type CompositeLogContext struct {
	ILogContext
	Tag string
}

func (ctx CompositeLogContext) GetTag() string {
	return ctx.Tag
}

type SimpleLogContext struct {
	Tag               string
	LogLevelThreshold logrus.Level
}

func NewSimpleLogContext() *SimpleLogContext {
	logCtx := &SimpleLogContext{
		Tag:               randutil.GenerateShortHexId(),
		LogLevelThreshold: logrus.InfoLevel,
	}

	return logCtx
}

func (ctx SimpleLogContext) GetTag() string {
	return ctx.Tag
}

func (ctx SimpleLogContext) GetLogLevelThreshold() logrus.Level {
	return ctx.LogLevelThreshold
}

func (ctx *SimpleLogContext) SetLogLevelThreshold(lvl logrus.Level) {
	ctx.LogLevelThreshold = lvl
}

func (ctx SimpleLogContext) IsTraceEnabled() bool {
	return ctx.LogLevelThreshold >= logrus.TraceLevel
}

func (ctx SimpleLogContext) IsDebugEnabled() bool {
	return ctx.LogLevelThreshold >= logrus.DebugLevel
}

func (ctx SimpleLogContext) IsInfoEnabled() bool {
	return ctx.LogLevelThreshold >= logrus.InfoLevel
}

func (ctx SimpleLogContext) IsWarnEnabled() bool {
	return ctx.LogLevelThreshold >= logrus.WarnLevel
}

func (ctx SimpleLogContext) IsErrorEnabled() bool {
	return ctx.LogLevelThreshold >= logrus.ErrorLevel
}
