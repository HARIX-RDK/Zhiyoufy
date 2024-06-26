package run

import (
	"context"
	"fmt"
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/prometheus/client_golang/prometheus/promhttp"
	log "github.com/sirupsen/logrus"

	"github.com/zhiyoufy/zhiyoufygo/pkg/jms"
	"github.com/zhiyoufy/zhiyoufygo/pkg/logp"
	"github.com/zhiyoufy/zhiyoufygo/pkg/script"
)

type runManager struct {
	cfg           *RunCmdConfig
	httpServer    *http.Server
	jobRunner     *jms.JobRunner
	jobFinishChan chan *jms.EventJobFinishData
}

func Run(cfg *RunCmdConfig) {
	log.Info("Run: begin")
	logp.LoadAndConfigure(cfg.AppName)
	log.Info("Run: After load and configure logging")

	manager := runManager{
		cfg: cfg,
	}

	err := manager.run()

	if err != nil {
		log.Errorf("run failed: %s", err)
	}
}

func (manager *runManager) run() error {
	cfg := manager.cfg

	var err error

	jobRunner := jms.NewJobRunner(*cfg.JobRunnerConfig)
	manager.jobRunner = jobRunner
	manager.jobFinishChan = make(chan *jms.EventJobFinishData)

	jobRunner.AddEventListener(manager.onEventJobFinish)

	script.RegisterMetrics()
	manager.runHttpServer()
	defer manager.stopHttpServer()

	if err = jobRunner.Run(); err != nil {
		return err
	}

	<-manager.jobFinishChan

	err = jobRunner.Stop()

	return err
}

func (manager *runManager) onEventJobFinish(data *jms.EventJobFinishData) {
	manager.jobFinishChan <- data
}

func (manager *runManager) runHttpServer() {
	r := gin.Default()
	addr := fmt.Sprintf(":%d", manager.cfg.HttpPort)
	srv := &http.Server{
		Addr:    addr,
		Handler: r,
	}
	manager.httpServer = srv

	r.GET("/metrics", promHandler())
	r.POST("/updateParallelNum", manager.onUpdateParallelNumReq())

	go func() {
		if err := srv.ListenAndServe(); err != http.ErrServerClosed {
			// Error starting or closing listener:
			log.Fatalf("HTTP server ListenAndServe: %v", err)
		}
	}()
}

func (manager *runManager) stopHttpServer() {
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
