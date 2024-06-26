from zhiyoufy.common.models import BaseResultErrorType

from .timer_event import TimerEvent


class WaitResponseTimerEvent(TimerEvent):
    def __init__(self, req_handler, guid=None):
        super().__init__(guid=guid)

        self.req_handler = req_handler

    def on_fired(self):
        self.req_handler.build_base_response(
            BaseResultErrorType.RES_ERR_TIMEOUT, 400)
