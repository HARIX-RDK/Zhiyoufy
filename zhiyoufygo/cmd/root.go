package cmd

import (
	"encoding/json"
	"flag"
	"fmt"
	"os"
	"path/filepath"

	log "github.com/sirupsen/logrus"
	"github.com/spf13/cobra"
	"github.com/spf13/pflag"
	"github.com/spf13/viper"
)

var appName string = "zhiyoufygo"
var cmdCfg CmdConfig = DefaultCmdConfig()

var (
	cfgFile        string
	defaultCfgFile string = fmt.Sprintf("./configs/%s.json", appName)
	localCfgFile   string = fmt.Sprintf("./configs/%s.local.json", appName)
	flagCfgFile    string
	rootCmd        = &cobra.Command{
		Use:   appName,
		Short: appName,
		Run:   runCmd.Run,
	}
)

func Execute() {
	if err := rootCmd.Execute(); err != nil {
		fmt.Fprintln(os.Stderr, err)
		os.Exit(1)
	}
}

func init() {
	cobra.OnInitialize(initConfig)

	pflag.CommandLine.AddGoFlagSet(flag.CommandLine)
	viper.BindPFlags(pflag.CommandLine)

	rootCmd.PersistentFlags().StringVar(&flagCfgFile, "config", "", fmt.Sprintf("config file (default is %s)", defaultCfgFile))
}

func initConfig() {
	cfgFile = flagCfgFile

	if cfgFile == "" {
		fileInfo, err := os.Stat(localCfgFile)
		if err == nil && !fileInfo.IsDir() {
			cfgFile = localCfgFile
		}
	}

	if cfgFile == "" {
		fileInfo, err := os.Stat(defaultCfgFile)
		if err == nil && !fileInfo.IsDir() {
			cfgFile = defaultCfgFile
		}
	}

	if cfgFile == "" {
		log.Panic("cfgFile is not set")
	}

	// Use config file from the flag.
	viper.SetConfigFile(cfgFile)

	viper.AutomaticEnv()

	if err := viper.ReadInConfig(); err == nil {
		fmt.Println("Using config file:", viper.ConfigFileUsed())

		logConfig := viper.GetString("logConfig")

		if _, err := os.Stat(logConfig); os.IsNotExist(err) {
			if filepath.IsAbs(logConfig) {
				log.Panicf("logConfig %s doesn't exist", logConfig)
			}
			dir := filepath.Dir(viper.ConfigFileUsed())
			logConfig = filepath.Join(dir, logConfig)
			viper.Set("logConfig", logConfig)
			fmt.Println("Update logConfig file:", logConfig)
		}

		if _, err := os.Stat(logConfig); os.IsNotExist(err) {
			log.Panicf("Updated logConfig %s also doesn't exist", logConfig)
		}
	}

	content, err := os.ReadFile(cfgFile)

	if err != nil {
		log.Panicf("read cfgFile %s failed, %s", cfgFile, err)
	} else {
		err = json.Unmarshal(content, &cmdCfg)

		if err != nil {
			log.Panicf("parse cmdCfg %+v failed, %s", cmdCfg, err)
		}
	}
}
