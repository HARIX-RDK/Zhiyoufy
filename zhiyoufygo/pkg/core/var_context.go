package core

import (
	"fmt"
	"sync"

	"github.com/zhiyoufy/zhiyoufygo/pkg/util/collectionutil"
)

type IVarContext interface {
	GetVar(path string) (any, bool)
	GetVarTypeBool(varPath string) (bool, error)
	GetVarTypeFloat64(varPath string) (float64, error)
	GetVarTypeInt(varPath string) (int, error)
	GetVarTypeString(varPath string) (string, error)
	SetVar(path string, value any)
}

type SimpleVarContext struct {
	varLock sync.RWMutex
	varMap  map[string]any
}

func NewSimpleVarContext() *SimpleVarContext {
	return &SimpleVarContext{
		varMap: make(map[string]any),
	}
}

func (ctx *SimpleVarContext) GetVar(path string) (any, bool) {
	ctx.varLock.RLock()
	defer ctx.varLock.RUnlock()

	value, err := collectionutil.DeepGet(ctx.varMap, path)
	if err != nil {
		return nil, false
	} else {
		return value, true
	}
}

func (ctx *SimpleVarContext) GetVarTypeBool(varPath string) (bool, error) {
	varValue, ok := ctx.GetVar(varPath)
	if !ok {
		return false, fmt.Errorf("varPath %s not exist", varPath)
	}

	value, ok := varValue.(bool)
	if ok {
		return value, nil
	}

	return value, fmt.Errorf("varPath %s, varValue %v is not a float64", varPath, varValue)
}

func (ctx *SimpleVarContext) GetVarTypeFloat64(varPath string) (float64, error) {
	varValue, ok := ctx.GetVar(varPath)
	if !ok {
		return 0, fmt.Errorf("varPath %s not exist", varPath)
	}

	value, ok := varValue.(float64)
	if ok {
		return value, nil
	}

	return value, fmt.Errorf("varPath %s, varValue %v is not a float64, but is %T", varPath, varValue, varValue)
}

func (ctx *SimpleVarContext) GetVarTypeInt(varPath string) (int, error) {
	varValue, ok := ctx.GetVar(varPath)
	if !ok {
		return 0, fmt.Errorf("varPath %s not exist", varPath)
	}

	switch value := varValue.(type) {
	case int:
		return value, nil
	case int64:
		return int(value), nil
	case float64:
		return int(value), nil
	default:
		return 0, fmt.Errorf("varPath %s, varValue %v is not a int, but is %T", varPath, varValue, varValue)
	}
}

func (ctx *SimpleVarContext) GetVarTypeString(varPath string) (string, error) {
	varValue, ok := ctx.GetVar(varPath)
	if !ok {
		return "", fmt.Errorf("varPath %s not exist", varPath)
	}

	value, ok := varValue.(string)
	if ok {
		return value, nil
	}

	return value, fmt.Errorf("varPath %s, varValue %v is not a string", varPath, varValue)
}

func (ctx *SimpleVarContext) SetVar(path string, value any) {
	ctx.varLock.Lock()
	defer ctx.varLock.Unlock()

	collectionutil.DeepSet(ctx.varMap, path, value)
}
