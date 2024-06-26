package zhiyoufyworker

type WorkerEventType string

type WorkerEvent struct {
	Type WorkerEventType
	Data any
}
