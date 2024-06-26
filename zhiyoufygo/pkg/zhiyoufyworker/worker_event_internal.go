package zhiyoufyworker

import "time"

var (
	eventConnectToServer         WorkerEventType = "eventConnectToServer"
	eventJmsJobFinish            WorkerEventType = "eventJmsJobFinish"
	eventRegisterTimeout         WorkerEventType = "eventRegisterTimeout"
	eventStop                    WorkerEventType = "eventStop"
	eventStompChannelStateUpdate WorkerEventType = "eventStompChannelStateUpdate"
)

type eventRegisterTimeoutData struct {
	timer     *time.Timer
	cancelled bool
}
