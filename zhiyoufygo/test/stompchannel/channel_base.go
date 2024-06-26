package stompchannel

import (
	"time"

	"github.com/zhiyoufy/zhiyoufygo/pkg/stompchannel"
	"github.com/zhiyoufy/zhiyoufygo/test"
)

var subProtocols = []string{"v12.stomp"}
var taskConnectTimeout time.Duration = time.Second * 200

func NewChannelController() *stompchannel.ChannelController {
	config := stompchannel.ChannelControllerConfig{
		ServerAddr: test.TestConfig.ZhiyoufyServerAddr,
	}
	controller := stompchannel.NewChannelController(config)

	return controller
}
