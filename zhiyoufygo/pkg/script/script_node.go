package script

type ScriptNode struct {
	Name string
	Path string

	BlockContextMap    map[string]*BlockContext
	CommandList        []ICommand
	CommandDisplayList []string
	CommandNsList      []string
	NodeList           []*ScriptNode

	parent *ScriptNode
}

func (node ScriptNode) HasCommands() bool {
	return node.CommandList != nil
}

func (node ScriptNode) HasNodes() bool {
	return node.NodeList != nil
}
