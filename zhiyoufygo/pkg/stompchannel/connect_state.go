package stompchannel

type ConnectState string

const (
	Disconnected  ConnectState = "Disconnected"
	Connecting    ConnectState = "Connecting"
	Connected     ConnectState = "Connected"
	Disconnecting ConnectState = "Disconnecting"
)
