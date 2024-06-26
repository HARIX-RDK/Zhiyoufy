package script

type ICommandHandler interface {
	GetCommandNamespace() string
	ParseCommand(commandId string, data []byte) (ICommand, error)
	HandleCommand(runCtx *ScriptRunContext, icmd ICommand) error
}
