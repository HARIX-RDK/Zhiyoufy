package logp

import (
	"encoding/json"
	"fmt"
	"io"
	"log"
	"os"
	"path/filepath"

	"github.com/spf13/viper"
	"gopkg.in/natefinch/lumberjack.v2"

	flag "github.com/spf13/pflag"

	"github.com/sirupsen/logrus"
)

func init() {
	flag.String("logConfig", "log.json", "Configure log")
}

// Logging builds a logp.Config based on configs.
func LoadAndConfigure(appName string) error {
	logConfig := viper.GetString("logConfig")

	content, err := os.ReadFile(logConfig)

	if err != nil {
		log.Panicf("logging: read logConfig %s failed, %s", logConfig, err)
	}

	return ParseAndConfigure(appName, content)
}

func ParseAndConfigure(appName string, content []byte) error {
	config := DefaultConfig()
	config.AppName = appName

	err := json.Unmarshal(content, &config)

	if err != nil {
		log.Panicf("logging: parse logConfig failed, %s", err)
	}

	lvl, err := logrus.ParseLevel(config.Level)

	if err != nil {
		log.Panicf("logging: invalid log level %s", config.Level)
	}

	config.logrusLevel = lvl

	return Configure(config)
}

// Configure configures the logp package.
func Configure(cfg Config) error {
	log.Println("Configure: enter to configure logp")

	if cfg.JSON {
		logrus.SetFormatter(&logrus.JSONFormatter{TimestampFormat: "2006-01-02T15:04:05.000-07:00"})
	} else {
		logrus.SetFormatter(&logrus.TextFormatter{
			DisableColors:   true,
			DisableQuote:    true,
			FullTimestamp:   true,
			TimestampFormat: "2006-01-02T15:04:05.000-07:00",
		})
	}

	logrus.SetReportCaller(cfg.ReportCaller)
	logrus.SetLevel(cfg.logrusLevel)

	var fileWriter *lumberjack.Logger

	if cfg.ToFiles {
		fileName := filepath.Join(cfg.Files.Path, fmt.Sprintf("%s.log", cfg.AppName))

		fileWriter = &lumberjack.Logger{
			Filename:   fileName,
			MaxSize:    cfg.Files.MaxSize,
			MaxBackups: cfg.Files.MaxBackups,
			MaxAge:     cfg.Files.MaxAge,
			Compress:   cfg.Files.Compress,
		}

		if cfg.RotateOnStartup {
			fileWriter.Rotate()
		}
	}

	if cfg.ToFiles && cfg.ToConsole {
		multiWriter := io.MultiWriter(os.Stdout, fileWriter)
		logrus.SetOutput(multiWriter)
	} else if cfg.ToFiles {
		logrus.SetOutput(fileWriter)
	} else {
		logrus.SetOutput(os.Stdout)
	}

	logrus.Info("Configure: finish configure logp")

	return nil
}
