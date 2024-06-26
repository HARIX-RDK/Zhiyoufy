package core

import "github.com/zhiyoufy/zhiyoufygo/pkg/util/collectionutil"

type IConfigContext interface {
	GetConfig(path string) (any, bool)
	SetConfig(path string, value any)
}

type SimpleConfigContext struct {
	configMap map[string]any
}

func NewSimpleConfigContext(configMap map[string]any) *SimpleConfigContext {
	configContext := &SimpleConfigContext{
		configMap: configMap,
	}
	if configMap == nil {
		configContext.configMap = make(map[string]any)
	}
	return configContext
}

func (ctx SimpleConfigContext) GetConfig(path string) (any, bool) {
	value, err := collectionutil.DeepGet(ctx.configMap, path)
	if err != nil {
		return nil, false
	} else {
		return value, true
	}
}

func (ctx *SimpleConfigContext) SetConfig(path string, value any) {
	collectionutil.DeepSet(ctx.configMap, path, value)
}
