package script

type ICommand interface {
	GetCommandId() string
	GetCatchPointIndex() int
	SetCatchPointIndex(index int)
	IsCatchPoint() bool
	Check() error
}
