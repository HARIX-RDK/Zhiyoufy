package basecmd

import (
	"errors"
)

const DownloadFileCommandId = BaseCommandNamespace + "/DownloadFileCommand"

type DownloadFileCommandState struct {
	Data *DownloadFileData
}

type DownloadFileData struct {
	FileUrl    string `json:"fileUrl"`
	FileDigest string `json:"fileDigest"`
	LocalPath  string `json:"localPath"`
}

type DownloadFileCommand struct {
	Data        *DownloadFileData `json:"data"`
	DataVarPath string            `json:"dataVarPath"`
	DataCfgPath string            `json:"dataCfgPath"`

	catchPointIndex int
}

func (cmd DownloadFileCommand) GetCommandId() string {
	return DownloadFileCommandId
}

func (cmd DownloadFileCommand) GetCatchPointIndex() int {
	return cmd.catchPointIndex
}

func (cmd *DownloadFileCommand) SetCatchPointIndex(index int) {
	cmd.catchPointIndex = index
}

func (cmd DownloadFileCommand) IsCatchPoint() bool {
	return false
}

func (cmd DownloadFileCommand) Check() error {
	if cmd.Data != nil && (cmd.DataVarPath != "" || cmd.DataCfgPath != "") {
		return errors.New("data is set, but dataVarPath or dataCfgPath is also set")
	}
	if cmd.DataVarPath != "" && cmd.DataCfgPath != "" {
		return errors.New("dataVarPath and dataCfgPath are both set")
	}

	if cmd.Data == nil && cmd.DataVarPath == "" && cmd.DataCfgPath == "" {
		return errors.New("data, dataVarPath and dataCfgPath are all empty")
	}

	return nil
}
