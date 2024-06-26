package run

import (
	"github.com/zhiyoufy/zhiyoufygo/pkg/jms"
	"github.com/zhiyoufy/zhiyoufygo/pkg/util/randutil"
)

type RunCmdConfig struct {
	AppName         string               `json:"appName"`
	RunIdPrefix     string               `json:"runIdPrefix"`
	HttpPort        int                  `json:"httpPort"`
	JobRunnerConfig *jms.JobRunnerConfig `json:"jobRunnerConfig"`
}

func DefaultConfig() *RunCmdConfig {
	cfg := RunCmdConfig{
		RunIdPrefix: randutil.GenerateShortHexId(),
		HttpPort:    9090,
	}
	return &cfg
}
