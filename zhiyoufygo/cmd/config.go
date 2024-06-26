package cmd

import (
	"github.com/zhiyoufy/zhiyoufygo/pkg/cmd/run"
	"github.com/zhiyoufy/zhiyoufygo/pkg/cmd/worker"
)

type CmdConfig struct {
	RunCmdCfg    *run.RunCmdConfig       `json:"runCmdCfg"`
	WorkerCmdCfg *worker.WorkerCmdConfig `json:"workerCmdCfg"`
}

// DefaultConfig returns the default config options.
func DefaultCmdConfig() CmdConfig {
	cfg := CmdConfig{}
	cfg.RunCmdCfg = run.DefaultConfig()
	cfg.WorkerCmdCfg = worker.DefaultConfig()

	return cfg
}
