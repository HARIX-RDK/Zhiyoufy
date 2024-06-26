package cmd

import (
	"fmt"

	"github.com/spf13/cobra"
	"github.com/zhiyoufy/zhiyoufygo/pkg/cmd/worker"
	"github.com/zhiyoufy/zhiyoufygo/pkg/script"

	jsonplaceholder_client "github.com/zhiyoufy/zhiyoufygo/pkg/clients/jsonplaceholder"
	"github.com/zhiyoufy/zhiyoufygo/pkg/handlers/jsonplaceholder"
)

var metricsRegistered bool

func init() {
	rootCmd.AddCommand(workerCmd)
}

var workerCmd = &cobra.Command{
	Use:   "worker",
	Short: fmt.Sprintf("worker command of %s", appName),
	Run: func(cmd *cobra.Command, args []string) {
		workerCmdConfig := cmdCfg.WorkerCmdCfg

		workerCmdConfig.AppName = appName

		scriptRunnerCustomizer := func(runner *script.ScriptJobRunner) error {
			if !metricsRegistered {
				jsonplaceholder_client.RegisterMetrics()
				metricsRegistered = true
			}

			commandHandler := jsonplaceholder.NewCommandHandler()
			if err := runner.RegisterCommandHandler(commandHandler); err != nil {
				return err
			}

			return nil
		}

		workerCmdConfig.WorkerCfg.ScriptRunnerCustomizer = scriptRunnerCustomizer

		worker.Run(cmdCfg.WorkerCmdCfg)
	},
}
