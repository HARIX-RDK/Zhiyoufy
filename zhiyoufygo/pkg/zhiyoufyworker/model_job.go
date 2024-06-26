package zhiyoufyworker

import (
	"github.com/zhiyoufy/zhiyoufygo/pkg/core/api"
	"github.com/zhiyoufy/zhiyoufygo/pkg/script"
)

type jmsStartJobChildRunReq struct {
	RunGuid         string `json:"runGuid"`
	EnvironmentId   int    `json:"environmentId"`
	EnvironmentName string `json:"environmentName"`
	TemplateId      int    `json:"templateId"`
	TemplateName    string `json:"templateName"`
	RunNum          int    `json:"runNum"`
	ParallelNum     int    `json:"parallelNum"`
	Index           int    `json:"index"`
	JobPath         string `json:"jobPath"`
	TimeoutSeconds  int    `json:"timeoutSeconds"`
	BaseConfPath    string `json:"baseConfPath"`
	PrivateConfPath string `json:"privateConfPath"`
	ConfigComposite string `json:"configComposite"`
	ExtraArgs       string `json:"extraArgs"`
}

type jmsStartJobChildRunRsp struct {
	Error     *api.ErrorInfo `json:"error"`
	RunGuid   string         `json:"runGuid"`
	Index     int            `json:"index"`
	MessageId string         `json:"messageId"`
}

type jmsUpdatePerfParallelNumReq struct {
	RunGuid         string `json:"runGuid"`
	PerfParallelNum int    `json:"perfParallelNum"`
}

type jmsStopJobChildRunReq struct {
	RunGuid string `json:"runGuid"`
	Index   int    `json:"index"`
}

type jmsStopJobChildRunRsp struct {
	Error     *api.ErrorInfo `json:"error"`
	RunGuid   string         `json:"runGuid"`
	Index     int            `json:"index"`
	MessageId string         `json:"messageId"`
}

type jmsJobChildRunResultInd struct {
	MessageId         string                         `json:"messageId"`
	RunGuid           string                         `json:"runGuid"`
	Index             int                            `json:"index"`
	EndOk             bool                           `json:"endOk"`
	ResultOk          bool                           `json:"resultOk"`
	Passed            bool                           `json:"passed"`
	JobOutputUrl      string                         `json:"jobOutputUrl"`
	ChildStatusCntMap map[script.ScriptJobStatus]int `json:"childStatusCntMap"`
}

type jmsJobChildRunResultRsp struct {
	MessageId string `json:"messageId"`
	RunGuid   string `json:"runGuid"`
	Index     int    `json:"index"`
}
