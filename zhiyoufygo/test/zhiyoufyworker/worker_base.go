package zhiyoufyworker

import (
	"testing"
	"time"

	"github.com/zhiyoufy/zhiyoufygo/pkg/util/randutil"
	"github.com/zhiyoufy/zhiyoufygo/pkg/zhiyoufyworker"
	"github.com/zhiyoufy/zhiyoufygo/test"
)

func NewZhiyoufyWorker(t *testing.T) *zhiyoufyworker.ZhiyoufyWorker {
	config := LoadConfig(t)
	config.AppRunId = randutil.GenerateShortHexId()
	config.AppStartTimestamp = time.Now().Format(time.RFC3339)

	worker := zhiyoufyworker.NewZhiyoufyWorker(config)

	return worker
}

func LoadConfig(t *testing.T) zhiyoufyworker.ZhiyoufyWorkerConfig {
	config := zhiyoufyworker.ZhiyoufyWorkerConfig{
		ServerAddr: test.TestConfig.ZhiyoufyServerAddr,
	}
	test.LoadConfigByPath(t, "configs/worker_config.json", &config)

	return config
}
