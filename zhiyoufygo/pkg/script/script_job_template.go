package script

type ScriptJobTemplate struct {
	JobId        string
	JobName      string
	Script       string
	Config       string
	SessionName  string
	AccountingId string
}
