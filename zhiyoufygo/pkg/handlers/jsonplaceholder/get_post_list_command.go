package jsonplaceholder

const GetPostListCommandId = CommandNamespace + "/GetPostListCommand"

type GetPostListCommand struct {
	catchPointIndex int
}

func (cmd GetPostListCommand) GetCommandId() string {
	return GetPostListCommandId
}

func (cmd GetPostListCommand) GetCatchPointIndex() int {
	return cmd.catchPointIndex
}

func (cmd *GetPostListCommand) SetCatchPointIndex(index int) {
	cmd.catchPointIndex = index
}

func (cmd GetPostListCommand) IsCatchPoint() bool {
	return false
}

func (cmd GetPostListCommand) Check() error {
	return nil
}
