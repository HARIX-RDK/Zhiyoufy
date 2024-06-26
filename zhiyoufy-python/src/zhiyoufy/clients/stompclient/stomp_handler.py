import copy
from datetime import datetime, timedelta
import threading

from zhiyoufy.stomper import StompDecoder, BufferingStompDecoder, Frame
from zhiyoufy.common.utils import RandomUtil, StrUtil

from .stomp_client_bridge import StompClientBridge
from .stomp_subscription import StompSubscription


class StompHandler(StompClientBridge):
    HEARTBEAT_MSG = b"\n"

    def __init__(self, parent_client):
        super().__init__(parent_client=parent_client)

        self.logger = self.robot_logger

        self._websocket_client = None
        self.connected = False
        self.dispose_requested = False

        self._counter = 0
        self._receipt_watchers = {}
        self._subscriptions = {}

        self.stomp_connect_timer = None
        self.stomp_connect_timeout = 20

        self.stomp_decoder = None
        self.buffering_stomp_decoder = None

        # heartbeat related
        self.default_heart_beat = (20_000, 20_000)
        self.last_recv_msg_time = None
        self.recv_heartbeat_period = None
        self.next_send_heartbeat_time = None
        self.send_heartbeat_period = None
        self.heartbeat_thread = None
        self.heartbeat_condition = threading.Condition()

    def set_websocket_client(self, websocket_client):
        self._websocket_client = websocket_client

        websocket_client.on_message = self._on_websocket_message

    def dispose(self):
        log_prefix = f"{self.log_prefix} dispose:"

        self.dispose_requested = True

        if self.heartbeat_thread:
            with self.heartbeat_condition:
                self.heartbeat_condition.notify()

        if self.connected:
            disconnect_headers = copy.copy(self.base_client.disconnect_headers)
            self._counter += 1
            receipt = f"close-{self._counter}"
            disconnect_headers["receipt"] = receipt

            self._receipt_watchers[receipt] = self._on_receipt_disconnect

            stomp_msg = Frame()
            stomp_msg.cmd = "DISCONNECT"
            stomp_msg.headers = disconnect_headers
            stomp_msg_packed = stomp_msg.pack()
            msg_id = RandomUtil.gen_guid()

            self.logger.info(f"{log_prefix} send DISCONNECT msg_id {msg_id} {stomp_msg_packed}")

            self._websocket_client.send_message(stomp_msg_packed, msg_id)
        else:
            self._websocket_client.disconnect()

    def on_websocket_open(self, connect_session):
        log_prefix = f"{self.log_prefix} on_websocket_open:"

        self.stomp_decoder = StompDecoder()
        self.buffering_stomp_decoder = BufferingStompDecoder(self.stomp_decoder)

        self.last_recv_msg_time = None
        self.recv_heartbeat_period = None
        self.send_heartbeat_period = None
        self.next_send_heartbeat_time = None

        connect_headers = copy.copy(self.base_client.connect_headers)
        connect_headers['accept-version'] = "1.2"
        connect_headers['heart-beat'] = f"{self.default_heart_beat[0]},{self.default_heart_beat[1]}"

        stomp_msg = Frame()
        stomp_msg.cmd = "CONNECT"
        stomp_msg.headers = connect_headers
        stomp_msg_packed = stomp_msg.pack()
        msg_id = RandomUtil.gen_guid()

        self.logger.info(f"{log_prefix} send connect_req msg_id {msg_id} {stomp_msg_packed}")

        self._websocket_client.send_message(stomp_msg_packed, msg_id)

    def publish(self, params):
        log_prefix = f"{self.log_prefix} publish:"

        destination = params["destination"]
        headers = copy.copy(params["headers"])
        body = params.get("body")

        headers["destination"] = destination

        stomp_msg = Frame()
        stomp_msg.cmd = "SEND"
        stomp_msg.headers = headers
        if body:
            stomp_msg.body = body
        stomp_msg_packed = stomp_msg.pack()
        msg_id = RandomUtil.gen_guid()

        self.logger.info(f"{log_prefix} send SEND msg_id {msg_id} {stomp_msg_packed}")

        self._websocket_client.send_message(stomp_msg_packed, msg_id)

    def subscribe(self, destination, callback, headers):
        log_prefix = f"{self.log_prefix} subscribe:"

        headers = copy.copy(headers)

        if "id" not in headers:
            self._counter += 1
            headers["id"] = f"sub-{self._counter}"

        headers["destination"] = destination
        subscription_id = headers["id"]
        self._subscriptions[subscription_id] = callback

        self.logger.info(f"{log_prefix} destination {destination} id {subscription_id}")

        stomp_msg = Frame()
        stomp_msg.cmd = "SUBSCRIBE"
        stomp_msg.headers = headers
        stomp_msg_packed = stomp_msg.pack()
        msg_id = RandomUtil.gen_guid()

        self.logger.info(f"{log_prefix} send SUBSCRIBE msg_id {msg_id} {stomp_msg_packed}")

        self._websocket_client.send_message(stomp_msg_packed, msg_id)

        subscription = StompSubscription(id=subscription_id)

        return subscription

    def unsubscribe(self, id, headers):
        log_prefix = f"{self.log_prefix} unsubscribe:"

        self.logger.info(f"{log_prefix} id {id}")

        self._subscriptions.pop(id, None)

        headers = copy.copy(headers)
        headers["id"] = id

        stomp_msg = Frame()
        stomp_msg.cmd = "UNSUBSCRIBE"
        stomp_msg.headers = headers
        stomp_msg_packed = stomp_msg.pack()
        msg_id = RandomUtil.gen_guid()

        self.logger.info(f"{log_prefix} send UNSUBSCRIBE msg_id {msg_id} {stomp_msg_packed}")

        self._websocket_client.send_message(stomp_msg_packed, msg_id)

    def _on_websocket_message(self, message):
        log_prefix = f"{self.log_prefix} _on_websocket_message:"

        self.last_recv_msg_time = datetime.utcnow()

        if message == self.HEARTBEAT_MSG:
            self.logger.debug(f"{self.log_prefix}, recv HEARTBEAT_MSG")
            return

        self.logger.info("%s message: %s" % (log_prefix, message))

        frames = self.buffering_stomp_decoder.decode(message)

        for frame in frames:
            self._on_frame(frame)

    def _on_websocket_close(self, connect_session):
        self.connected = False

    def _on_frame(self, frame):
        log_prefix = f"{self.log_prefix} _on_frame:"

        self.logger.info(f"{log_prefix} frame {StrUtil.pprint(frame)}")

        frame_cmd = frame["cmd"]

        if frame_cmd == "CONNECTED":
            self._on_frame_cmd_connected(frame)
        elif frame_cmd == "MESSAGE":
            self._on_frame_cmd_message(frame)
        else:
            self.logger.info(f"{log_prefix} cmd {frame_cmd}")

    def _on_frame_cmd_connected(self, frame):
        self.connected = True

        server_heart_beat = frame["headers"].get("heart-beat", None)

        if server_heart_beat:
            sx = int(server_heart_beat.split(",")[0])
            sy = int(server_heart_beat.split(",")[1])
            cx = self.default_heart_beat[0]
            cy = self.default_heart_beat[1]
            c2s = max(cx, sy)
            if c2s > 0:
                self.send_heartbeat_period = float(c2s) / 1000 - 3
                self.next_send_heartbeat_time = datetime.utcnow() + \
                                                timedelta(seconds=self.send_heartbeat_period)
            s2c = max(cy, sx)
            if s2c > 0:
                self.recv_heartbeat_period = float(s2c) / 1000 + 10

            self.heartbeat_thread = threading.Thread(target=self._heartbeat_run, daemon=True)
            self.heartbeat_thread.start()

    def _on_frame_cmd_message(self, frame):
        subscription_id = frame["headers"].get("subscription")

        if not subscription_id:
            return

        on_receive = self._subscriptions.get(subscription_id)

        if not on_receive:
            return

        on_receive(frame)

    def _on_receipt_disconnect(self):
        self._websocket_client.on_message = None
        self._websocket_client.disconnect()

    def _heartbeat_run(self):
        while not self.dispose_requested:
            with self.heartbeat_condition:
                if not self._check_recv_heartbeat():
                    self.heartbeat_thread = None

                    self._websocket_client.disconnect()

                    return

                self._send_heartbeat_if_needed()

                self.heartbeat_condition.wait(1)

        self.heartbeat_thread = None

    def _check_recv_heartbeat(self):
        if not self.recv_heartbeat_period:
            return True

        if datetime.utcnow() < (self.last_recv_msg_time + timedelta(seconds=self.recv_heartbeat_period)):
            return True

        return False

    def _send_heartbeat_if_needed(self):
        if self.next_send_heartbeat_time is None:
            return

        time_diff = self.next_send_heartbeat_time - datetime.utcnow()

        if timedelta(seconds=1) < time_diff < timedelta(seconds=self.send_heartbeat_period+10):
            return

        self.logger.debug(f"{self.log_prefix} to send HEARTBEAT_MSG")
        self._websocket_client.send_message(self.HEARTBEAT_MSG)

        self.next_send_heartbeat_time = \
            datetime.utcnow() + timedelta(seconds=self.send_heartbeat_period)
