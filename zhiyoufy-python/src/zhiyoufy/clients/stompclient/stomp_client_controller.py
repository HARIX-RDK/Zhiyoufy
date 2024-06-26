import itertools

from zhiyoufy.common.utils import LogUtil, StrUtil

from .stomp_client_bridge import StompClientBridge
from .stomp_controller_endpoint import StompControllerEndpoint
from .stomp_rpc_stream import StompRpcStream


class StompClientController(StompClientBridge):
    controller_endpoint_prefix = "/controller"

    def __init__(self, parent_client):
        super().__init__(parent_client=parent_client)

        self.logger = self.robot_logger

        self.controller_id = None
        self.local_controller_destination = None
        self.subscription = None
        self._stream_id_iter = itertools.count(1)

        self._endpoints = {}
        self._streams = {}

        ping_endpoint = StompControllerEndpoint(app_destination=f"{self.controller_endpoint_prefix}/ping",
                                                callback=self._on_ping)
        self.controller_register_endpoint(ping_endpoint)

    def controller_configure(self, params):
        log_prefix = f"{self.log_prefix} controller_configure:"

        self.logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            self.controller_id = params["controller_id"]
            self.local_controller_destination = f"/topic/stomp-controller-{self.controller_id}"
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def controller_create_stream(self):
        log_prefix = f"{self.log_prefix} controller_create_stream:"

        stream_num = next(self._stream_id_iter)
        stream_id = f"{self.controller_id}-{stream_num}"
        new_stream = StompRpcStream(id=stream_id)

        self.logger.info(f"{log_prefix} stream_id {stream_id} streams len {len(self._streams)}")

        self._streams[stream_id] = new_stream

        return new_stream

    def controller_del_stream(self, stream):
        log_prefix = f"{self.log_prefix} controller_del_stream:"

        stream_id = stream.id

        self.logger.info(f"{log_prefix} stream_id {stream_id} streams len {len(self._streams)}")

        self._streams.pop(stream_id)

    def controller_register_endpoint(self, endpoint):
        log_prefix = f"{self.log_prefix} controller_register_endpoint:"

        self.logger.info(f"{log_prefix} Enter app_destination {endpoint.app_destination}")

        self._endpoints[endpoint.app_destination] = endpoint

    def controller_send_message(self, out_message, in_message=None):
        if "headers" not in out_message:
            out_message["headers"] = {}

        out_message_headers = out_message["headers"]
        out_message_headers["replyDestination"] = self.local_controller_destination

        if in_message:
            in_message_headers = in_message.headers

            out_message["destination"] = in_message_headers["replyDestination"]

            if "replyAppDestination" in in_message_headers:
                out_message_headers["appDestination"] = in_message_headers["replyAppDestination"]

            out_message_headers["streamId"] = in_message_headers["streamId"]

        self.base_client.base_publish(out_message)

    def controller_start(self, params):
        log_prefix = f"{self.log_prefix} controller_start:"

        self.logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            self.subscription = self.base_client.base_subscribe(
                self.local_controller_destination, self._on_controller_message)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def _on_controller_message(self, in_message):
        log_prefix = f"{self.log_prefix} _on_controller_message:"

        self.logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(in_message)))

        in_message_headers = in_message["headers"]
        app_destination = in_message_headers.get("appDestination")
        stream_id = in_message_headers.get("streamId")

        if app_destination:
            endpoint = self._endpoints.get(app_destination)

            if not endpoint:
                self.logger.error(f"{log_prefix} ignore unexpected app_destination {app_destination}")
                return

            endpoint.callback(in_message)

            return

        if stream_id:
            rpc_stream = self._streams.get(stream_id)

            if not rpc_stream:
                self.logger.error(f"{log_prefix} ignore unexpected stream_id {stream_id}")
                return

            with rpc_stream.condition:
                rpc_stream.message_list.append(in_message)
                rpc_stream.condition.notify()

            return

        self.logger.error(f"{log_prefix} ignore unexpected headers {StrUtil.pprint(in_message_headers)}")

    def _on_ping(self, frame):
        pass
