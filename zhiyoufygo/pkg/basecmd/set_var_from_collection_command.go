package basecmd

import (
	"errors"
	"fmt"
	"slices"
)

const SetVarFromCollectionCommandId = BaseCommandNamespace + "/SetVarFromCollectionCommand"

type SetVarFromCollectionCommand struct {
	BeCatchPoint          bool   `json:"isCatchPoint"`
	VarPath               string `json:"varPath"`
	CollectionType        string `json:"collectionType"`
	CollectionVarPath     string `json:"collectionVarPath"`
	ArrayIdxUseChildIdx   bool   `json:"arrayIdxUseChildIdx"`
	ArrayIdxStrideVarPath string `json:"arrayIdxStrideVarPath"`
	ArrayIdx              int    `json:"arrayIdx"`
	MapKey                string `json:"mapKey"`

	catchPointIndex int
}

func (cmd SetVarFromCollectionCommand) GetCommandId() string {
	return SetVarFromCollectionCommandId
}

func (cmd SetVarFromCollectionCommand) GetCatchPointIndex() int {
	return cmd.catchPointIndex
}

func (cmd *SetVarFromCollectionCommand) SetCatchPointIndex(index int) {
	cmd.catchPointIndex = index
}

func (cmd SetVarFromCollectionCommand) IsCatchPoint() bool {
	return cmd.BeCatchPoint
}

func (cmd SetVarFromCollectionCommand) Check() error {
	if cmd.VarPath == "" {
		return errors.New("varPath is empty")
	}

	collectionTypeSet := []string{"array", "map"}

	if !slices.Contains(collectionTypeSet, cmd.CollectionType) {
		return fmt.Errorf("invalid collection type %s", cmd.CollectionType)
	}

	if cmd.CollectionVarPath == "" {
		return errors.New("collectionVarPath is empty")
	}

	if cmd.CollectionType == "array" {
		if !cmd.ArrayIdxUseChildIdx && cmd.ArrayIdx < 0 {
			return fmt.Errorf("invalid arrayIdx %d", cmd.ArrayIdx)
		}
	} else if cmd.CollectionType == "map" {
		if cmd.MapKey == "" {
			return errors.New("mapKey is empty")
		}
	}

	return nil
}
