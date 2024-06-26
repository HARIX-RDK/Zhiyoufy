import pytest
import time
from datetime import datetime, timedelta
import contextlib
import functools
import logging

from zhiyoufy.common.models import BaseEvent

from zhiyoufy.worker.app import WorkerApp, WorkerAppEventType
from zhiyoufy.worker.master import MasterChannelSignal, MasterChannelEventType


class TestWorkerApp:
    @pytest.fixture(autouse=True)
    def setup(self, init_app):
        self.log_prefix = f"{type(self).__name__}:"
        self.logger = logging.getLogger(type(self).__name__)

        self.event_out_worker_register_req = None

    def test_connected_ok(self, config_inst):
        with contextlib.ExitStack() as stack:
            worker_app = WorkerApp()

            worker_app.start()
            stack.callback(worker_app.stop)

            MasterChannelSignal.TO_MASTER_CHANNEL_EVENT.connect(self.on_event)
            stack.callback(functools.partial(MasterChannelSignal.TO_MASTER_CHANNEL_EVENT.disconnect, self.on_event))

            master_connected_event = BaseEvent(MasterChannelEventType.MASTER_CHANNEL_CONNECTED)
            MasterChannelSignal.FROM_MASTER_CHANNEL_EVENT.send(self, event=master_connected_event)

            final_time = datetime.utcnow() + timedelta(seconds=10)

            while datetime.utcnow() <= final_time:
                if not self.event_out_worker_register_req:
                    time.sleep(0.02)
                    continue
                break

            assert self.event_out_worker_register_req

    def on_event(self, sender, event):
        log_prefix = f"{self.log_prefix} on_event:"

        self.logger.debug(f"{log_prefix} event {event}")

        if event.event_type == MasterChannelEventType.TO_MASTER_STOMP_MESSAGE:
            stomp_msg = event.content_extra["stomp_msg"]

            if b"/app/worker-register" in stomp_msg:
                self.event_out_worker_register_req = event
