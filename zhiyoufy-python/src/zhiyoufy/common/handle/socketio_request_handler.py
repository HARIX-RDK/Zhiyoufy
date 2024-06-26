from zhiyoufy.common.utils import RandomUtil, TimeUtil

from .base_in_request_handler import BaseInRequestHandler
from .req_src import ReqSrc


class SocketIORequestHandler(BaseInRequestHandler):
    def __init__(self, event=None, **kwargs):
        super().__init__(req_src=ReqSrc.WebSocket, **kwargs)

        self.event = event

        if (self.user_name and self.user_name in self.global_context.need_trace_records) or \
                "*" in self.global_context.need_trace_records:
            self.handle_context["need_trace_records"] = True
        else:
            self.handle_context["need_trace_records"] = False


    @property
    def json_data(self):
        if self._json_data is None:
            self._json_data = self.event.content["body"] if "body" in self.event.content else {}
        return self._json_data

    def build_base_response(self, err_enum, status_code=None, err_detail=None, exception=None):
        dumped_response = super().build_base_response(
            err_enum, status_code=status_code, err_detail=err_detail, exception=exception)
        return dumped_response, status_code

    def socketio_build_response_skin(self):
        rsp_msg = {
            "type": "BASE_RESPONSE",
            "guid": RandomUtil.gen_guid(),
            "timestamp": TimeUtil.get_current_time_isoformat(),
            "req_guid": self.event.content["guid"],
        }
        return rsp_msg
