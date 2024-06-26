package logp

import "github.com/sirupsen/logrus"

// Config contains the configuration options for the logger.
type Config struct {
	AppName string `json:"-"`     // Name of the App (for default file name).
	JSON    bool   `json:"json"`  // Write logs as JSON.
	Level   string `json:"level"` // Logging level (error, warning, info, debug).

	ToConsole bool `json:"to_console"`
	ToFiles   bool `json:"to_files"`

	RotateOnStartup bool       `json:"rotate_on_startup"`
	Files           FileConfig `json:"files"`

	ReportCaller bool `json:"report_caller"` // Adds package and line number info to messages.

	logrusLevel logrus.Level
}

// FileConfig contains the configuration options for the file output.
type FileConfig struct {
	Path       string `json:"path"`
	MaxSize    int    `json:"maxsize"`
	MaxBackups int    `json:"maxbackups"`
	MaxAge     int    `json:"maxage"`
	Compress   bool   `json:"compress"`
}

var defaultConfig = Config{
	JSON:            false,
	Level:           "info",
	ToConsole:       true,
	ToFiles:         true,
	RotateOnStartup: true,
	Files: FileConfig{
		Path:       "logs",
		MaxSize:    10,
		MaxBackups: 20,
		MaxAge:     10,
	},
	ReportCaller: false,
	logrusLevel:  logrus.InfoLevel,
}

// DefaultConfig returns the default config options.
func DefaultConfig() Config {
	return defaultConfig
}
