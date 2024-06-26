package core

import (
	"context"
	"crypto/tls"
	"net/http"
	"time"

	"github.com/zhiyoufy/zhiyoufygo/pkg/core/bus"
)

var (
	CustomTransport *http.Transport
)

func init() {
	defaultTransport := http.DefaultTransport.(*http.Transport)
	CustomTransport = &http.Transport{
		Proxy:                 defaultTransport.Proxy,
		DialContext:           defaultTransport.DialContext,
		ForceAttemptHTTP2:     defaultTransport.ForceAttemptHTTP2,
		MaxIdleConns:          defaultTransport.MaxIdleConns,
		IdleConnTimeout:       defaultTransport.IdleConnTimeout,
		TLSHandshakeTimeout:   defaultTransport.TLSHandshakeTimeout,
		ExpectContinueTimeout: defaultTransport.ExpectContinueTimeout,
		TLSClientConfig: &tls.Config{
			InsecureSkipVerify: true,
		},
	}
}

type IBaseRunContext interface {
	GetRootContext() context.Context
	GetHttpClient() *http.Client
}

type IRunContext interface {
	bus.Bus
	IBaseRunContext
	IConfigContext
	ILogContext
	IVarContext
}

type CompositeRunContext struct {
	bus.Bus
	IBaseRunContext
	IConfigContext
	ILogContext
	IVarContext
}

func (runCtx *CompositeRunContext) FillDefault() {
	if runCtx.Bus == nil {
		runCtx.Bus = bus.New()
	}

	if runCtx.IBaseRunContext == nil {
		runCtx.IBaseRunContext = NewSimpleBaseRunContext()
	}

	if runCtx.IConfigContext == nil {
		runCtx.IConfigContext = NewSimpleConfigContext(nil)
	}

	if runCtx.ILogContext == nil {
		runCtx.ILogContext = NewSimpleLogContext()
	}

	if runCtx.IVarContext == nil {
		runCtx.IVarContext = NewSimpleVarContext()
	}
}

type SimpleBaseRunContext struct {
	Ctx        context.Context
	HttpClient *http.Client
}

func NewSimpleBaseRunContext() *SimpleBaseRunContext {
	runCtx := &SimpleBaseRunContext{}

	runCtx.Ctx = context.Background()
	client := &http.Client{Timeout: time.Duration(20) * time.Second, Transport: CustomTransport}
	runCtx.HttpClient = client

	return runCtx
}

func (runCtx *SimpleBaseRunContext) GetRootContext() context.Context {
	return runCtx.Ctx
}

func (runCtx *SimpleBaseRunContext) GetHttpClient() *http.Client {
	return runCtx.HttpClient
}
