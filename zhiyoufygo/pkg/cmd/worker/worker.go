package worker

import (
	"context"
	"fmt"
	"net/http"
	"os"
	"strings"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/prometheus/client_golang/prometheus/promhttp"
	log "github.com/sirupsen/logrus"

	"github.com/zhiyoufy/zhiyoufygo/pkg/logp"
	"github.com/zhiyoufy/zhiyoufygo/pkg/script"
	"github.com/zhiyoufy/zhiyoufygo/pkg/util/randutil"
	"github.com/zhiyoufy/zhiyoufygo/pkg/zhiyoufyworker"
)

type workerManager struct {
	cmdConfig  *WorkerCmdConfig
	cfg        *zhiyoufyworker.ZhiyoufyWorkerConfig
	httpServer *http.Server
}

func Run(cmdConfig *WorkerCmdConfig) {
	log.Info("WorkerRun: begin")
	logp.LoadAndConfigure(cmdConfig.AppName)
	log.Info("WorkerRun: After load and configure logging")

	if cmdConfig.WorkerCfg == nil {
		log.Error("WorkerRun: workerCfg not set")
		return
	}

	podName := os.Getenv("MY_POD_NAME")

	if podName != "" {
		cmdConfig.WorkerCfg.WorkerName = fmt.Sprintf("%s-%s", cmdConfig.WorkerCfg.WorkerName, podName)
		if strings.HasSuffix(cmdConfig.WorkerCfg.JobOutputUrl, "/") {
			cmdConfig.WorkerCfg.JobOutputUrl = fmt.Sprintf("%s%s/", cmdConfig.WorkerCfg.JobOutputUrl, podName)
		} else {
			cmdConfig.WorkerCfg.JobOutputUrl = fmt.Sprintf("%s/%s/", cmdConfig.WorkerCfg.JobOutputUrl, podName)
		}
	}

	manager := workerManager{
		cmdConfig: cmdConfig,
		cfg:       cmdConfig.WorkerCfg,
	}

	err := manager.run()

	if err != nil {
		log.Errorf("run failed: %s", err)
	}
}

func (manager *workerManager) run() error {
	cfg := manager.cfg

	var err error

	cfg.AppRunId = randutil.GenerateShortHexId()
	cfg.AppStartTimestamp = time.Now().Format(time.RFC3339)

	worker := zhiyoufyworker.NewZhiyoufyWorker(*cfg)

	script.RegisterMetrics()
	manager.runHttpServer()
	defer manager.stopHttpServer()

	if err = worker.Run(); err != nil {
		return err
	}

	for {
		<-time.NewTimer(10 * time.Second).C
	}
}

func (manager *workerManager) runHttpServer() {
	r := gin.Default()
	addr := fmt.Sprintf(":%d", manager.cfg.HttpPort)
	srv := &http.Server{
		Addr:    addr,
		Handler: r,
	}
	manager.httpServer = srv

	r.StaticFS("/zhiyoufy-worker", http.Dir("./logs"))
	r.GET("/metrics", promHandler())

	go func() {
		if err := srv.ListenAndServe(); err != http.ErrServerClosed {
			// Error starting or closing listener:
			log.Fatalf("HTTP server ListenAndServe: %v", err)
		}
	}()
}

func (manager workerManager) stopHttpServer() {
	srv := manager.httpServer

	err := srv.Shutdown(context.Background())

	if err != nil {
		log.Errorf("HTTP server Shutdown: %v", err)
	} else {
		log.Info("HTTP server Shutdown: ok")
	}
}

func promHandler() gin.HandlerFunc {
	h := promhttp.Handler()
	return func(c *gin.Context) {
		h.ServeHTTP(c.Writer, c.Request)
	}
}
