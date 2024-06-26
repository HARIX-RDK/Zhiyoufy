package basecmd

import "fmt"

const TriggerErrorCommandId = BaseCommandNamespace + "/TriggerErrorCommand"

type TriggerErrorCommand struct {
	ErrorProbability float64 `json:"errorProbability"`

	catchPointIndex int
}

func (cmd TriggerErrorCommand) GetCommandId() string {
	return TriggerErrorCommandId
}

func (cmd TriggerErrorCommand) GetCatchPointIndex() int {
	return cmd.catchPointIndex
}

func (cmd *TriggerErrorCommand) SetCatchPointIndex(index int) {
	cmd.catchPointIndex = index
}

func (cmd TriggerErrorCommand) IsCatchPoint() bool {
	return false
}

func (cmd TriggerErrorCommand) Check() error {
	if cmd.ErrorProbability < 0 {
		return fmt.Errorf("ErrorProbability %.2f is minus", cmd.ErrorProbability)
	}
	return nil
}
