package jsonplaceholder

import (
	"encoding/json"
	"fmt"

	"github.com/zhiyoufy/zhiyoufygo/pkg/clients/jsonplaceholder"
	"github.com/zhiyoufy/zhiyoufygo/pkg/script"
)

type CommandHandler struct {
}

func NewCommandHandler() *CommandHandler {
	handler := CommandHandler{}
	return &handler
}

func (handler CommandHandler) GetCommandNamespace() string {
	return CommandNamespace
}

func (handler CommandHandler) ParseCommand(commandId string, data []byte) (script.ICommand, error) {
	var icmd script.ICommand
	var err error

	switch commandId {
	case CreateClientCommandId:
		cmd := CreateClientCommand{BaseUrl: jsonplaceholder.DefaultBaseUrl}
		icmd = &cmd
		err = json.Unmarshal(data, &cmd)

		if err != nil {
			return nil, err
		}
	case CreatePostCommandId:
		cmd := CreatePostCommand{}
		icmd = &cmd
		err = json.Unmarshal(data, &cmd)

		if err != nil {
			return nil, err
		}
	case GetPostByIdCommandId:
		cmd := GetPostByIdCommand{}
		icmd = &cmd
		err = json.Unmarshal(data, &cmd)

		if err != nil {
			return nil, err
		}
	case GetPostListCommandId:
		cmd := GetPostListCommand{}
		icmd = &cmd
	case UpdatePostCommandId:
		cmd := UpdatePostCommand{}
		icmd = &cmd
		err = json.Unmarshal(data, &cmd)

		if err != nil {
			return nil, err
		}
	default:
		return nil, fmt.Errorf("invalid commandId %s", commandId)
	}

	if err = icmd.Check(); err != nil {
		return nil, err
	}

	return icmd, nil
}

func (handler CommandHandler) HandleCommand(runCtx *script.ScriptRunContext, icmd script.ICommand) error {
	switch cmd := icmd.(type) {
	case *CreateClientCommand:
		return handler.handleCreateClientCommand(runCtx, cmd)
	case *CreatePostCommand:
		return handler.handleCreatePostCommand(runCtx, cmd)
	case *GetPostByIdCommand:
		return handler.handleGetPostByIdCommand(runCtx, cmd)
	case *GetPostListCommand:
		return handler.handleGetPostListCommand(runCtx, cmd)
	case *UpdatePostCommand:
		return handler.handleUpdatePostCommand(runCtx, cmd)
	}

	return fmt.Errorf("%s not handled", runCtx.LogPrefix)
}

func (handler CommandHandler) handleCreateClientCommand(runCtx *script.ScriptRunContext, cmd *CreateClientCommand) error {
	client := jsonplaceholder.NewClient(jsonplaceholder.ClientConfig{
		RunContext: runCtx,
		BaseUrl:    cmd.BaseUrl,
	})
	runCtx.SetVar(varPathClient, client)

	return nil
}

func (handler CommandHandler) handleCreatePostCommand(runCtx *script.ScriptRunContext, cmd *CreatePostCommand) error {
	client, err := getClient(runCtx)

	if err != nil {
		return err
	}

	_, err = client.CreatePost(runCtx.GetRootContext(), cmd.Data)

	return err
}

func (handler CommandHandler) handleGetPostByIdCommand(runCtx *script.ScriptRunContext, cmd *GetPostByIdCommand) error {
	client, err := getClient(runCtx)

	if err != nil {
		return err
	}

	_, err = client.GetPostById(runCtx.GetRootContext(), cmd.PostId)

	return err
}

func (handler CommandHandler) handleGetPostListCommand(runCtx *script.ScriptRunContext, cmd *GetPostListCommand) error {
	client, err := getClient(runCtx)

	if err != nil {
		return err
	}

	_, err = client.GetPostList(runCtx.GetRootContext())

	return err
}

func (handler CommandHandler) handleUpdatePostCommand(runCtx *script.ScriptRunContext, cmd *UpdatePostCommand) error {
	client, err := getClient(runCtx)

	if err != nil {
		return err
	}

	_, err = client.UpdatePost(runCtx.GetRootContext(), cmd.Data)

	return err
}

func getClient(runCtx *script.ScriptRunContext) (*jsonplaceholder.Client, error) {
	varValue, ok := runCtx.GetVar(varPathClient)

	if !ok {
		return nil, fmt.Errorf("no var exist in path %s", varPathClient)
	}

	client, ok := varValue.(*jsonplaceholder.Client)

	if !ok {
		return nil, fmt.Errorf("var in path %s is not of type *jsonplaceholder.Client, but %T", varPathClient, varValue)
	}

	return client, nil
}
