package zhiyoufyworker

type EventStateChange struct {
}

type EventServerStopped struct{}

type EventRegisterStateUpdate struct {
	Registered bool
}
