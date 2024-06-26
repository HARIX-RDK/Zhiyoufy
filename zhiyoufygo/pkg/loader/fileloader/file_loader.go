package fileloader

import (
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"io/fs"
	"os"
	"sync"
	"time"

	log "github.com/sirupsen/logrus"

	"github.com/zhiyoufy/zhiyoufygo/pkg/core"
	"github.com/zhiyoufy/zhiyoufygo/pkg/core/cache"
)

var (
	DefaultFileLoader *FileLoader = &FileLoader{
		cache:       cache.New(5*time.Minute, time.Minute),
		workChanMap: make(map[string]chan struct{}),
		mutex:       &sync.Mutex{},
	}
)

func Load(ctx context.Context, logCtx core.ILogContext, fs fs.ReadFileFS, filePath string) ([]byte, error) {
	return DefaultFileLoader.Load(ctx, logCtx, fs, filePath)
}

func LoadJsonArray(ctx context.Context, logCtx core.ILogContext, fs fs.ReadFileFS, filePath string) ([]any, error) {
	return DefaultFileLoader.LoadJsonArray(ctx, logCtx, fs, filePath)
}

type FileLoader struct {
	cache       *cache.Cache
	workChanMap map[string]chan struct{}
	mutex       *sync.Mutex
}

func (loader *FileLoader) Load(ctx context.Context, logCtx core.ILogContext, fs fs.ReadFileFS, filePath string) ([]byte, error) {
	content, ok := loader.cache.Get(filePath)
	if ok {
		return content.([]byte), nil
	}

	loader.mutex.Lock()

	content, ok = loader.cache.Get(filePath)
	if ok {
		loader.mutex.Unlock()
		return content.([]byte), nil
	}

	workChan, workChanOk := loader.workChanMap[filePath]
	if !workChanOk {
		workChan = make(chan struct{})
		loader.workChanMap[filePath] = workChan
	}

	loader.mutex.Unlock()

	if workChanOk {
		select {
		case <-ctx.Done():
			return nil, ctx.Err()
		case <-workChan:
		}
	} else {
		content, err := loader.doLoad(logCtx, fs, filePath)
		if err == nil {
			loader.cache.SetDefault(filePath, content)
		}

		loader.mutex.Lock()
		delete(loader.workChanMap, filePath)
		close(workChan)
		loader.mutex.Unlock()

		return content, err
	}

	content, ok = loader.cache.Get(filePath)
	if ok {
		return content.([]byte), nil
	} else {
		return nil, errors.New("file load failed")
	}
}

func (loader *FileLoader) LoadJsonArray(ctx context.Context, logCtx core.ILogContext, fs fs.ReadFileFS, filePath string) ([]any, error) {
	storeKey := fmt.Sprintf("%s__JsonArray", filePath)

	content, ok := loader.cache.Get(storeKey)
	if ok {
		contentJsonArray, ok := content.([]any)
		if ok {
			return contentJsonArray, nil
		} else {
			return nil, fmt.Errorf("stored data type is not []any, but is %T", content)
		}
	}

	contentBytes, err := loader.Load(ctx, logCtx, fs, filePath)
	if err != nil {
		return nil, err
	}

	loader.mutex.Lock()

	content, ok = loader.cache.Get(storeKey)
	if ok {
		loader.mutex.Unlock()
		contentJsonArray, ok := content.([]any)
		if ok {
			return contentJsonArray, nil
		} else {
			return nil, fmt.Errorf("stored data type is not []any, but is %T", content)
		}
	}

	var contentJsonArray []any
	err = json.Unmarshal(contentBytes, &contentJsonArray)

	if err != nil {
		loader.mutex.Unlock()
		return nil, fmt.Errorf("failed to parse as json array")
	}

	loader.cache.SetDefault(storeKey, contentJsonArray)

	loader.mutex.Unlock()

	return contentJsonArray, nil
}

func (loader *FileLoader) doLoad(logCtx core.ILogContext, fs fs.ReadFileFS, filePath string) ([]byte, error) {
	var content []byte
	var err error

	if fs != nil {
		content, err = fs.ReadFile(filePath)
	} else {
		content, err = os.ReadFile(filePath)
	}

	log.Infof("%s doLoad filePath %s, err %s", logCtx.GetTag(), filePath, err)

	return content, err
}
