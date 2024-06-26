package worker

import "github.com/zhiyoufy/zhiyoufygo/pkg/zhiyoufyworker"

type WorkerCmdConfig struct {
	AppName   string                               `json:"appName"`
	WorkerCfg *zhiyoufyworker.ZhiyoufyWorkerConfig `json:"workerCfg"`
}

func DefaultConfig() *WorkerCmdConfig {
	cfg := WorkerCmdConfig{}
	return &cfg
}
