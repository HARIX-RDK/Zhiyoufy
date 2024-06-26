package run

import (
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/zhiyoufy/zhiyoufygo/pkg/core/api"
	"github.com/zhiyoufy/zhiyoufygo/pkg/jms"
)

func (manager *runManager) onUpdateParallelNumReq() gin.HandlerFunc {
	return func(c *gin.Context) {
		var req updateParallelNumReq

		if err := c.BindJSON(&req); err != nil {
			return
		}

		data := &jms.EventUpdateParallelNumData{
			ParallelNum: req.ParallelNum,
		}
		manager.jobRunner.SendEvent(jms.EventUpdateParallelNum, data)

		rsp := api.BaseResponse{}

		c.IndentedJSON(http.StatusOK, rsp)
	}
}
