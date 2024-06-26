from zhiyoufy.common.dynamicflow.TestDynamicFlowBase import TestDynamicFlowBase as _TestDynamicFlowBase

from zhiyoufy.dynamicflow.DynamicFlowStompClient import DynamicFlowStompClient
from zhiyoufy.dynamicflow.DynamicFlowZhiyoufyClient import DynamicFlowZhiyoufyClient


class TestDynamicFlow(_TestDynamicFlowBase):
    ROBOT_LIBRARY_SCOPE = 'TEST'
    ROBOT_LIBRARY_VERSION = "0.1"

    def __init__(self):
        super().__init__()

        self.setup_libraries()

    def setup_libraries(self):
        self.add_handler(DynamicFlowStompClient())
        self.add_handler(DynamicFlowZhiyoufyClient())
