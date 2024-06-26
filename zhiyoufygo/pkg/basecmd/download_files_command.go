package basecmd

import (
	"errors"
)

const DownloadFilesCommandId = BaseCommandNamespace + "/DownloadFilesCommand"

type DownloadFilesCommandState struct {
	DataList []*DownloadFileData
}

type DownloadFilesCommand struct {
	DataList        []*DownloadFileData `json:"dataList"`
	DataListVarPath string              `json:"dataListVarPath"`
	DataListCfgPath string              `json:"dataListCfgPath"`

	catchPointIndex int
}

func (cmd DownloadFilesCommand) GetCommandId() string {
	return DownloadFilesCommandId
}

func (cmd DownloadFilesCommand) GetCatchPointIndex() int {
	return cmd.catchPointIndex
}

func (cmd *DownloadFilesCommand) SetCatchPointIndex(index int) {
	cmd.catchPointIndex = index
}

func (cmd DownloadFilesCommand) IsCatchPoint() bool {
	return false
}

func (cmd DownloadFilesCommand) Check() error {
	if cmd.DataList != nil && (cmd.DataListVarPath != "" || cmd.DataListCfgPath != "") {
		return errors.New("dataList is set, but dataListVarPath or dataListCfgPath is also set")
	}
	if cmd.DataListVarPath != "" && cmd.DataListCfgPath != "" {
		return errors.New("dataListVarPath and dataListCfgPath are both set")
	}

	if cmd.DataList == nil && cmd.DataListVarPath == "" && cmd.DataListCfgPath == "" {
		return errors.New("dataList, dataListVarPath and dataListCfgPath are all empty")
	}

	return nil
}
