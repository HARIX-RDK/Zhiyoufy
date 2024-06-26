from zhiyoufy.common.dynamicflow.CustomLibraryBase import CustomLibraryBase

from .stomp_client_controller import StompClientController
from .stomp_client_base import StompClientBase


# 参照stompjs接口封装
class StompClient(CustomLibraryBase):
    def __init__(self):
        super().__init__()

        self.controller_client = StompClientController(parent_client=self)
        self.base_client = StompClientBase(parent_client=self)
