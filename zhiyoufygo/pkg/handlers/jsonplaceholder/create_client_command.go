package jsonplaceholder

import "errors"

const CreateClientCommandId = CommandNamespace + "/CreateClientCommand"

type CreateClientCommand struct {
	BaseUrl string `json:"baseUrl"`

	catchPointIndex int
}

func (cmd CreateClientCommand) GetCommandId() string {
	return CreateClientCommandId
}

func (cmd CreateClientCommand) GetCatchPointIndex() int {
	return cmd.catchPointIndex
}

func (cmd *CreateClientCommand) SetCatchPointIndex(index int) {
	cmd.catchPointIndex = index
}

func (cmd CreateClientCommand) IsCatchPoint() bool {
	return false
}

func (cmd CreateClientCommand) Check() error {
	if cmd.BaseUrl == "" {
		return errors.New("baseUrl is empty")
	}

	return nil
}
