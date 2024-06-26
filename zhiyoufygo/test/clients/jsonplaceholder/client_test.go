package jsonplaceholder

import (
	"testing"

	log "github.com/sirupsen/logrus"

	"github.com/zhiyoufy/zhiyoufygo/pkg/clients/jsonplaceholder"

	"github.com/stretchr/testify/require"
	"github.com/zhiyoufy/zhiyoufygo/test"
	"github.com/zhiyoufy/zhiyoufygo/test/testutils"
)

func TestCreatePost(t *testing.T) {
	test.LoadConfig(t)

	log.Infof("test %s: after LoadConfig", t.Name())

	runCtx := testutils.NewCompositeRunContext(t, nil)
	client := NewClient(runCtx)

	req := jsonplaceholder.CreatePostReq{
		Title:  "foo",
		Body:   "bar",
		UserId: 1,
	}

	rsp, err := client.CreatePost(runCtx.GetRootContext(), req)

	require.True(t, err == nil)
	require.True(t, rsp != nil)
}

func TestGetPostById(t *testing.T) {
	test.LoadConfig(t)

	log.Infof("test %s: after LoadConfig", t.Name())

	runCtx := testutils.NewCompositeRunContext(t, nil)
	client := NewClient(runCtx)

	rsp, err := client.GetPostById(runCtx.GetRootContext(), 1)

	require.True(t, err == nil)
	require.True(t, rsp != nil)
}

func TestGetPostList(t *testing.T) {
	test.LoadConfig(t)

	log.Infof("test %s: after LoadConfig", t.Name())

	runCtx := testutils.NewCompositeRunContext(t, nil)
	client := NewClient(runCtx)

	rsp, err := client.GetPostList(runCtx.GetRootContext())

	require.True(t, err == nil)
	require.True(t, rsp != nil)
}

func TestUpdatePost(t *testing.T) {
	test.LoadConfig(t)

	log.Infof("test %s: after LoadConfig", t.Name())

	runCtx := testutils.NewCompositeRunContext(t, nil)
	client := NewClient(runCtx)

	req := jsonplaceholder.UpdatePostReq{
		ID:     1,
		Title:  "foo",
		Body:   "bar",
		UserId: 1,
	}

	rsp, err := client.UpdatePost(runCtx.GetRootContext(), req)

	require.True(t, err == nil)
	require.True(t, rsp != nil)
}
