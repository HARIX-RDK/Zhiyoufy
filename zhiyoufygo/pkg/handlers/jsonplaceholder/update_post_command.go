package jsonplaceholder

import (
	"errors"

	"github.com/zhiyoufy/zhiyoufygo/pkg/clients/jsonplaceholder"
)

const UpdatePostCommandId = CommandNamespace + "/UpdatePostCommand"

type UpdatePostCommand struct {
	Data jsonplaceholder.UpdatePostReq `json:"data"`

	catchPointIndex int
}

func (cmd UpdatePostCommand) GetCommandId() string {
	return UpdatePostCommandId
}

func (cmd UpdatePostCommand) GetCatchPointIndex() int {
	return cmd.catchPointIndex
}

func (cmd *UpdatePostCommand) SetCatchPointIndex(index int) {
	cmd.catchPointIndex = index
}

func (cmd UpdatePostCommand) IsCatchPoint() bool {
	return false
}

func (cmd UpdatePostCommand) Check() error {
	if cmd.Data.ID < 0 {
		return errors.New("id is less than 0")
	}

	if cmd.Data.Title == "" {
		return errors.New("title is empty")
	}

	if cmd.Data.Body == "" {
		return errors.New("body is empty")
	}

	if cmd.Data.UserId < 0 {
		return errors.New("userId is less than 0")
	}

	return nil
}
