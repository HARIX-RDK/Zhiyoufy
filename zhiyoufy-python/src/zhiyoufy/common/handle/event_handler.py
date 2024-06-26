from .base_in_request_handler import BaseInRequestHandler


class EventRequestHandler(BaseInRequestHandler):
    def __init__(self, event=None, **kwargs):
        super().__init__(**kwargs)

        self.event = event
