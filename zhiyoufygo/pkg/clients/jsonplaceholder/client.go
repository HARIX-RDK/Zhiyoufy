package jsonplaceholder

import (
	"context"
	"fmt"

	"github.com/zhiyoufy/zhiyoufygo/pkg/core"
)

var DefaultBaseUrl string = "https://jsonplaceholder.typicode.com"

type Client struct {
	core.IRunContext
	BaseUrl string
}

type ClientConfig struct {
	RunContext core.IRunContext
	BaseUrl    string
}

func NewClient(cfg ClientConfig) *Client {
	client := Client{
		IRunContext: cfg.RunContext,
	}

	if cfg.BaseUrl == "" {
		client.BaseUrl = DefaultBaseUrl
	} else {
		client.BaseUrl = cfg.BaseUrl
	}

	return &client
}

func (client Client) CreatePost(ctx context.Context, req CreatePostReq) (any, error) {
	reqUrl := fmt.Sprintf("%s/posts", client.BaseUrl)
	handler := "/posts"

	return client.httpPostAny(ctx, reqUrl, handler, req)
}

func (client Client) GetPostById(ctx context.Context, postId int) (any, error) {
	reqUrl := fmt.Sprintf("%s/posts/%d", client.BaseUrl, postId)
	handler := "/posts/:id"

	return client.httpGetAny(ctx, reqUrl, handler)
}

func (client Client) GetPostList(ctx context.Context) (any, error) {
	reqUrl := fmt.Sprintf("%s/posts", client.BaseUrl)
	handler := "/posts"

	return client.httpGetAny(ctx, reqUrl, handler)
}

func (client Client) UpdatePost(ctx context.Context, req UpdatePostReq) (any, error) {
	reqUrl := fmt.Sprintf("%s/posts/%d", client.BaseUrl, req.ID)
	handler := "/posts/:id"

	return client.httpPutAny(ctx, reqUrl, handler, req)
}
