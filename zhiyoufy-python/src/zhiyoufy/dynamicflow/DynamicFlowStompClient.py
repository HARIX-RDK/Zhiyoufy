from zhiyoufy.common.utils import CheckUtil


class DynamicFlowStompClient:
    def __init__(self):
        self.log_prefix = "%s: " % type(self).__name__
        self.type_prefix = "stomp_client_"

        self.config_inst = None
        self.robot_logger = None
        self.context = None

        self._stomp_client = None

    @property
    def stomp_client(self):
        return self.context.get_lib_client("stomp_client")

    def setup(self, dynamic_flow_base_inst):
        self.config_inst = dynamic_flow_base_inst.config_inst
        self.robot_logger = dynamic_flow_base_inst.robot_logger
        self.context = dynamic_flow_base_inst.context

        self.context.add_lib_client_factory("stomp_client", self.build_stomp_client)

    def cleanup(self, case_context):
        pass

    def build_stomp_client(self):
        if self._stomp_client is None:
            from zhiyoufy.clients.stompclient.stomp_client import StompClient
            self._stomp_client = StompClient()
        return self._stomp_client

    def process(self, data):
        if data['type'].startswith('stomp_client_base_'):
            self.process_type_of_stomp_client_base(data)
        elif data['type'].startswith('stomp_client_controller_'):
            self.process_type_of_stomp_client_controller(data)
        else:
            raise Exception("Unknown stomp_client type %s" % data['type'])

    def process_type_of_stomp_client_base(self, data):
        if data['type'] == 'stomp_client_base_activate':
            self.process_type_stomp_client_base_activate(data)
        elif data['type'] == 'stomp_client_base_configure':
            self.process_type_stomp_client_base_configure(data)
        elif data['type'] == 'stomp_client_base_deactivate':
            self.process_type_stomp_client_base_deactivate(data)
        elif data['type'] == 'stomp_client_base_wait_inactive':
            self.process_type_stomp_client_base_wait_inactive(data)
        elif data['type'] == 'stomp_client_base_wait_stomp_connected':
            self.process_type_stomp_client_base_wait_stomp_connected(data)
        else:
            raise Exception("Unknown stomp_client_base_ type %s" % data['type'])

    def process_type_of_stomp_client_controller(self, data):
        if data['type'] == 'stomp_client_controller_configure':
            self.process_type_stomp_client_controller_configure(data)
        elif data['type'] == 'stomp_client_controller_start':
            self.process_type_stomp_client_controller_start(data)
        else:
            raise Exception("Unknown stomp_client_controller_ type %s" % data['type'])

    # region TypeStompClientBase
    def process_type_stomp_client_base_activate(self, data):
        stomp_client = self.stomp_client

        stomp_client.base_client.base_activate()

    def process_type_stomp_client_base_configure(self, data):
        stomp_client = self.stomp_client

        stomp_client.base_client.base_configure(data)

    def process_type_stomp_client_base_wait_inactive(self, data):
        stomp_client = self.stomp_client

        is_inactive = stomp_client.base_client.base_wait_inactive(data)

        if not is_inactive:
            raise Exception("timeout not is_inactive")

    def process_type_stomp_client_base_wait_stomp_connected(self, data):
        stomp_client = self.stomp_client

        connected = stomp_client.base_client.base_wait_stomp_connected(data)

        if not connected:
            raise Exception("timeout not connected")

    def process_type_stomp_client_base_deactivate(self, data):
        stomp_client = self.stomp_client

        stomp_client.base_client.base_deactivate()
    # endregion

    # region TypeStompClientController
    def process_type_stomp_client_controller_configure(self, data):
        stomp_client = self.stomp_client

        stomp_client.controller_client.controller_configure(data)

    def process_type_stomp_client_controller_start(self, data):
        stomp_client = self.stomp_client

        stomp_client.controller_client.controller_start(data)
    # endregion
