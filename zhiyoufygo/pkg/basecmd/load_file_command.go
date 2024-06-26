package basecmd

import (
	"errors"
	"fmt"
	"slices"
)

const LoadFileCommandId = BaseCommandNamespace + "/LoadFileCommand"

type LoadFileCommandState struct {
	Data *LoadFileData
}

type LoadFileData struct {
	LocalPath     string `json:"localPath"`
	FileType      string `json:"fileType"`
	TargetVarPath string `json:"targetVarPath"`
}

type LoadFileCommand struct {
	Data        *LoadFileData `json:"data"`
	DataVarPath string        `json:"dataVarPath"`
	DataCfgPath string        `json:"dataCfgPath"`

	catchPointIndex int
}

func (cmd LoadFileCommand) GetCommandId() string {
	return LoadFileCommandId
}

func (cmd LoadFileCommand) GetCatchPointIndex() int {
	return cmd.catchPointIndex
}

func (cmd *LoadFileCommand) SetCatchPointIndex(index int) {
	cmd.catchPointIndex = index
}

func (cmd LoadFileCommand) IsCatchPoint() bool {
	return false
}

func (cmd LoadFileCommand) Check() error {
	if cmd.DataVarPath != "" && cmd.DataCfgPath != "" {
		return errors.New("dataVarPath and dataCfgPath are both set")
	}

	if cmd.Data == nil || cmd.Data.TargetVarPath == "" {
		return errors.New("data.targetVarPath is not set")
	}

	if cmd.Data.LocalPath == "" && cmd.DataVarPath == "" && cmd.DataCfgPath == "" {
		return errors.New("data.localPath, dataVarPath and dataCfgPath are all empty")
	}

	fileTypeSet := []string{"[]byte", "[]any"}

	if cmd.Data.FileType != "" && !slices.Contains(fileTypeSet, cmd.Data.FileType) {
		return fmt.Errorf("invalid file type %s", cmd.Data.FileType)
	}

	return nil
}
