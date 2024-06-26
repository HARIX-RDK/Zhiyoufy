package cmd

import (
	"fmt"

	"github.com/spf13/cobra"
	"github.com/zhiyoufy/zhiyoufygo/pkg/cmd/run"
	"github.com/zhiyoufy/zhiyoufygo/pkg/script"

	jsonplaceholder_client "github.com/zhiyoufy/zhiyoufygo/pkg/clients/jsonplaceholder"
	"github.com/zhiyoufy/zhiyoufygo/pkg/handlers/jsonplaceholder"
)

func init() {
	rootCmd.AddCommand(runCmd)
}

var runCmd = &cobra.Command{
	Use:   "run",
	Short: fmt.Sprintf("Main command of %s", appName),
	Run: func(cmd *cobra.Command, args []string) {
		runCmdConfig := cmdCfg.RunCmdCfg

		runCmdConfig.AppName = appName

		scriptRunnerCustomizer := func(runner *script.ScriptJobRunner) error {
			jsonplaceholder_client.RegisterMetrics()
			commandHandler := jsonplaceholder.NewCommandHandler()
			if err := runner.RegisterCommandHandler(commandHandler); err != nil {
				return err
			}

			return nil
		}

		runCmdConfig.JobRunnerConfig.ScriptRunnerCustomizer = scriptRunnerCustomizer

		run.Run(runCmdConfig)
	},
}
