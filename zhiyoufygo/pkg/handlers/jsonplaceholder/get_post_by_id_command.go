package jsonplaceholder

import (
	"errors"
)

const GetPostByIdCommandId = CommandNamespace + "/GetPostByIdCommand"

type GetPostByIdCommand struct {
	PostId int `json:"postId"`

	catchPointIndex int
}

func (cmd GetPostByIdCommand) GetCommandId() string {
	return GetPostByIdCommandId
}

func (cmd GetPostByIdCommand) GetCatchPointIndex() int {
	return cmd.catchPointIndex
}

func (cmd *GetPostByIdCommand) SetCatchPointIndex(index int) {
	cmd.catchPointIndex = index
}

func (cmd GetPostByIdCommand) IsCatchPoint() bool {
	return false
}

func (cmd GetPostByIdCommand) Check() error {
	if cmd.PostId < 0 {
		return errors.New("postId is less than 0")
	}

	return nil
}
