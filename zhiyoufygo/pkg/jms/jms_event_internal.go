package jms

import "time"

const (
	eventSpawnTimeout       JmsEventType = "eventSpawnTimeout"
	eventStart              JmsEventType = "eventStart"
	eventStop               JmsEventType = "eventStop"
	eventTaskChildJobRunEnd JmsEventType = "eventTaskChildJobRunEnd"
)

type eventSpawnTimeoutData struct {
	timer     *time.Timer
	cancelled bool
}
