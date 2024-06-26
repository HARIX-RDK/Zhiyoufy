package test

import (
	"bytes"
	"embed"
	"encoding/json"
	"os"
	"path/filepath"
	"runtime"
	"testing"
	"text/template"

	"github.com/zhiyoufy/zhiyoufygo/pkg/logp"
)

//go:embed configs
var ConfigsFS embed.FS

type Config struct {
	TestBasedir           string
	TestLogsDir           string
	TestDataDir           string
	TestDataLocalDir      string
	ZhiyoufyServerAddr    string `json:"zhiyoufyServerAddr"`
	BadZhiyoufyServerAddr string `json:"badZhiyoufyServerAddr"`
}

var TestConfig Config = Config{}

func LoadConfig(t *testing.T) {
	cfgFile := ""

	defaultCfgFile := "configs/zhiyoufygo.json"
	localCfgFile := "configs/zhiyoufygo.local.json"

	if cfgFile == "" {
		fileInfo, err := os.Stat(localCfgFile)
		if err == nil && !fileInfo.IsDir() {
			cfgFile = localCfgFile
		}
	}

	if cfgFile == "" {
		cfgFile = defaultCfgFile
	}

	content, err := ConfigsFS.ReadFile(cfgFile)

	if err != nil {
		t.Fatalf("read cfgFile %s failed, %s", cfgFile, err)
	}

	_, filename, _, _ := runtime.Caller(0)
	fileDir := filepath.Dir(filename)
	TestConfig.TestBasedir = fileDir
	TestConfig.TestLogsDir = filepath.Join(fileDir, "test_logs")
	TestConfig.TestDataDir = filepath.Join(fileDir, "testdata")
	TestConfig.TestDataLocalDir = filepath.Join(fileDir, "data.local")

	err = json.Unmarshal(content, &TestConfig)

	if err != nil {
		t.Fatalf("parse testConfig %+v failed, %s", TestConfig, err)
	}

	logCfgFile := "configs/log.json"

	tmpl := template.Must(template.New("log.json").Funcs(template.FuncMap{
		"json": func(v interface{}) (string, error) {
			b, err := json.Marshal(v)
			return string(b), err
		},
	}).ParseFS(ConfigsFS, logCfgFile))
	buf := bytes.Buffer{}

	err = tmpl.Execute(&buf, TestConfig)
	if err != nil {
		panic(err)
	}
	logConfigContent := buf.Bytes()

	err = logp.ParseAndConfigure("zhiyoufygo", logConfigContent)

	if err != nil {
		t.Fatal("logp.ParseAndConfigure failed")
	}
}

func LoadConfigByPath(t *testing.T, path string, cfg any) {
	cfgFile := path
	content, err := ConfigsFS.ReadFile(cfgFile)

	if err != nil {
		t.Fatalf("read cfgFile %s failed, %s", cfgFile, err)
	}

	err = json.Unmarshal(content, cfg)

	if err != nil {
		t.Fatalf("parse cfgFile %s failed, %s", cfgFile, err)
	}
}
