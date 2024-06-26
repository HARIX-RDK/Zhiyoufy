package jsonplaceholder

import (
	"errors"

	"github.com/zhiyoufy/zhiyoufygo/pkg/clients/jsonplaceholder"
)

const CreatePostCommandId = CommandNamespace + "/CreatePostCommand"

type CreatePostCommand struct {
	Data jsonplaceholder.CreatePostReq `json:"data"`

	catchPointIndex int
}

func (cmd CreatePostCommand) GetCommandId() string {
	return CreatePostCommandId
}

func (cmd CreatePostCommand) GetCatchPointIndex() int {
	return cmd.catchPointIndex
}

func (cmd *CreatePostCommand) SetCatchPointIndex(index int) {
	cmd.catchPointIndex = index
}

func (cmd CreatePostCommand) IsCatchPoint() bool {
	return false
}

func (cmd CreatePostCommand) Check() error {
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
