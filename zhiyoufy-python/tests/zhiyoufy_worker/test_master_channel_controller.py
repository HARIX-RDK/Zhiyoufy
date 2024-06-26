import pytest
import time
from datetime import datetime, timedelta
import contextlib

from zhiyoufy.worker.master import MasterChannelController, MasterChannelEventType


class TestMasterChannelController:
    @pytest.fixture(autouse=True)
    def setup(self, init_app):
        pass

    def test_connected_ok(self, config_inst):
        with contextlib.ExitStack() as stack:
            master_channel_controller = MasterChannelController()

            master_channel_controller.start()
            stack.callback(master_channel_controller.stop)

            master_channel_controller.send_simple_event_to_handler(MasterChannelEventType.MASTER_CHANNEL_CONNECT_REQ)

            final_time = datetime.utcnow() + timedelta(seconds=10)

            while datetime.utcnow() <= final_time:
                if not master_channel_controller.is_master_connected():
                    time.sleep(0.02)
                    continue
                break

            assert master_channel_controller.is_master_connected()
