from .timer_event import TimerEvent


class SendEventTimerEvent(TimerEvent):
    def __init__(self, handler_runnable, event, guid=None):
        super().__init__(guid=guid)

        self.handler_runnable = handler_runnable
        self.event = event

    def on_fired(self):
        self.handler_runnable.send_event_to_handler(self.event)
