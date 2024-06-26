package basecmd

import (
	"errors"
)

const SleepCommandId = BaseCommandNamespace + "/SleepCommand"

type SleepCommand struct {
	BeCatchPoint   bool     `json:"isCatchPoint"`
	TimeSeconds    *float64 `json:"timeSeconds"`
	TimeSecondsMin *float64 `json:"timeSecondsMin"`
	TimeSecondsMax *float64 `json:"timeSecondsMax"`

	catchPointIndex int
}

func (cmd SleepCommand) GetCommandId() string {
	return SleepCommandId
}

func (cmd SleepCommand) GetCatchPointIndex() int {
	return cmd.catchPointIndex
}

func (cmd *SleepCommand) SetCatchPointIndex(index int) {
	cmd.catchPointIndex = index
}

func (cmd SleepCommand) IsCatchPoint() bool {
	return cmd.BeCatchPoint
}

func (cmd SleepCommand) Check() error {
	if cmd.TimeSeconds != nil {
		if *cmd.TimeSeconds < 0 {
			return errors.New("timeSeconds is less than 0")
		}
	} else if cmd.TimeSecondsMin != nil {
		if *cmd.TimeSecondsMin < 0 {
			return errors.New("timeSecondsMin is less than 0")
		}
		if cmd.TimeSecondsMax == nil {
			return errors.New("timeSecondsMax is not specified while timeSecondsMin is set")
		}
		if *cmd.TimeSecondsMax < 0 {
			return errors.New("timeSecondsMax is less than 0")
		}
		if *cmd.TimeSecondsMax < *cmd.TimeSecondsMin {
			return errors.New("timeSecondsMax is less than timeSecondsMin")
		}
	} else {
		return errors.New("not specify how long to sleep")
	}

	return nil
}
