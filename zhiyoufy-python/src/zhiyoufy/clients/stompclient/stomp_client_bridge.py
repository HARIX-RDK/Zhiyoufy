from zhiyoufy.common.dynamicflow.CustomLibraryBridgeBase import CustomLibraryBridgeBase


class StompClientBridge(CustomLibraryBridgeBase):
    def __init__(self, parent_client):
        super().__init__(parent_client)
        self.parent_client = parent_client

    @property
    def controller_client(self):
        return self.parent_client.controller_client

    @property
    def base_client(self):
        return self.parent_client.base_client

    def get_broker_url(self):
        return self.base_client.broker_url

    def get_connect_headers(self):
        return self.base_client.connect_headers

    def check_stomp_connected(self):
        self.base_client.check_stomp_connected()
