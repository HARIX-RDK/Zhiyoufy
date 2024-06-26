package script

type ScriptRootNode struct {
	ScriptNode

	ScriptName    string
	FinallyNode   *ScriptNode
	MaxCommandSeq int
}

func (node ScriptRootNode) HasFinallyNode() bool {
	return node.FinallyNode != nil
}
