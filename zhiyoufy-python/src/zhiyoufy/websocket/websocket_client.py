import logging
import threading
import ssl
from datetime import datetime, timedelta
import time
import sys

import websocket

from zhiyoufy.common import GeneralConnectSession, GeneralConnectState, \
    zhiyoufy_context, config
from zhiyoufy.common.utils import RandomUtil


class WebsocketClient:
    def __init__(self, server_url, subprotocols=[],
                 on_open=None, on_message=None, on_close=None,
                 auto_reconnect=True):
        self.logger = logging.getLogger("zhiyoufy.websocket.WebsocketClient")
        self.tag = "%s" % type(self).__name__
        self.log_prefix = "%s:" % type(self).__name__

        self.config_inst = zhiyoufy_context.get_config_inst()
        self.lock = threading.RLock()

        self.server_url = server_url
        self.subprotocols = subprotocols

        self.on_open = on_open
        self.on_message = on_message
        self.on_close = on_close

        self._client_connect_state = GeneralConnectState.DISCONNECTED
        self._channel_connect_state = GeneralConnectState.DISCONNECTED
        self.connect_session = None
        self.websocket_inst = None
        self.auto_reconnect = auto_reconnect
        self.channel_end_expected = True

        # recv direction
        self.channel_recv_thread = None

        # send direction
        self.channel_send_thread = None
        self.channel_send_list = []
        self.channel_send_list_condition = threading.Condition()

        # connect related
        self.reconnect_timer = None
        self.connect_trial_cnt = 0
        self.reconnect_base_interval = 2
        self.reconnect_backoff_idx = 0
        self.reconnect_backoff_seq = [1, 1, 2, 4, 8, 16, 32, 60]

        self._init_module_config()

        self.websocket_client_config = self.config_inst.websocket_client

    def _init_module_config(self):
        config_inst = self.config_inst

        if "websocket_client" not in config_inst:
            config_inst.websocket_client = config.Params()

        websocket_client_config = config_inst.websocket_client

        if "connect_timeout" not in websocket_client_config:
            websocket_client_config.connect_timeout = 5

        if "reconnect_backoff_at_max_log_interval" not in websocket_client_config:
            websocket_client_config.reconnect_backoff_at_max_log_interval = 1800

    @property
    def reconnect_backoff_at_max(self):
        if self.reconnect_backoff_idx == (len(self.reconnect_backoff_seq) - 1):
            return True
        else:
            return False

    def is_client_connected(self):
        if self._client_connect_state == GeneralConnectState.CONNECTED:
            return True
        return False

    def is_client_connecting(self):
        if self._client_connect_state == GeneralConnectState.CONNECTING:
            return True
        return False

    def is_client_disconnecting(self):
        if self._client_connect_state == GeneralConnectState.DISCONNECTING:
            return True
        return False

    def is_client_disconnected(self):
        if self._client_connect_state == GeneralConnectState.DISCONNECTED:
            return True
        return False

    def is_channel_connected(self):
        if self._channel_connect_state == GeneralConnectState.CONNECTED:
            return True
        return False

    def is_channel_connecting(self):
        if self._channel_connect_state == GeneralConnectState.CONNECTING:
            return True
        return False

    def is_channel_disconnecting(self):
        if self._channel_connect_state == GeneralConnectState.DISCONNECTING:
            return True
        return False

    def is_channel_disconnected(self):
        if self._channel_connect_state == GeneralConnectState.DISCONNECTED:
            return True
        return False

    def set_client_connect_state(self, new_state):
        log_prefix = f"{self.log_prefix} set_client_connect_state:"

        self.logger.info(f"{log_prefix} from {self._client_connect_state} to {new_state}")

        self._client_connect_state = new_state

        if new_state == GeneralConnectState.DISCONNECTED:
            if self.websocket_inst:
                self.websocket_inst.close()
                self.websocket_inst = None

    def set_channel_connect_state(self, new_state):
        log_prefix = f"{self.log_prefix} set_channel_connect_state:"

        self.logger.info(f"{log_prefix} from {self._channel_connect_state} to {new_state}")

        self._channel_connect_state = new_state

    def connect(self):
        log_prefix = f"{self.log_prefix} connect:"

        self.logger.info(f"{log_prefix} Enter")

        with self.lock:
            if not self.is_client_disconnected():
                err_msg = f"{log_prefix} Invalid state {self._client_connect_state}"
                self.logger.error(err_msg)
                raise Exception(err_msg)

            self._reset_connect_session_data()

            self.set_client_connect_state(GeneralConnectState.CONNECTING)
            self.connect_session = GeneralConnectSession(session_id=RandomUtil.gen_guid())

            self.logger.info(f"{log_prefix} Enter session_id {self.connect_session.session_id}")

            threading.Timer(0, self._do_connect, args=[self.connect_session]).start()

    def disconnect(self):
        log_prefix = f"{self.log_prefix} disconnect:"

        self.logger.info(f"{log_prefix} Enter")

        with self.lock:
            if self.is_client_disconnected():
                self.logger.info(f"{log_prefix} Already disconnected")
                return

            if not (self.is_client_connecting() or self.is_client_connected()):
                err_msg = f"{log_prefix} Invalid state {self._client_connect_state}"
                self.logger.error(err_msg)
                raise Exception(err_msg)

            self.set_client_connect_state(GeneralConnectState.DISCONNECTING)

            if self.is_channel_connected():
                self._do_channel_disconnect()
            else:
                if self.reconnect_timer:
                    self.reconnect_timer.cancel()
                    self.reconnect_timer = None

                self.connect_session = None
                self.set_client_connect_state(GeneralConnectState.DISCONNECTED)

    def send_message(self, msg, msg_id=None):
        with self.channel_send_list_condition:
            self.channel_send_list.append((msg, msg_id))
            self.channel_send_list_condition.notify()

    def _reset_connect_session_data(self):
        self.connect_session = None
        self.websocket_inst = None
        self.channel_end_expected = False

        # recv direction
        self.channel_recv_thread = None

        # send direction
        self.channel_send_thread = None
        self.channel_send_list = []

        # reconnect related
        self._reset_reconnect_data()

    def _reset_reconnect_data(self):
        self.reconnect_timer = None
        self.connect_trial_cnt = 0
        self.reconnect_backoff_idx = 0

    def _do_connect(self, connect_session):
        log_prefix = f"{self.log_prefix} _do_connect:"

        self.logger.info(f"{log_prefix} Enter session_id {connect_session.session_id}")

        with self.lock:
            if not self.is_client_connecting() or connect_session != self.connect_session:
                return

            self.connect_trial_cnt += 1
            self.set_channel_connect_state(GeneralConnectState.CONNECTING)

        websocket_inst = None
        try:
            websocket_inst = websocket.create_connection(
                self.server_url,
                timeout=self.websocket_client_config.connect_timeout,
                enable_multithread=True,
                subprotocols=self.subprotocols,
                sslopt={"cert_reqs": ssl.CERT_NONE})
        except Exception as ex:
            self.logger.info(f"{log_prefix} create_connection met exception {ex}")

        with self.lock:
            if not self.is_client_connecting() or connect_session != self.connect_session:
                return

            if not websocket_inst:
                self.set_channel_connect_state(GeneralConnectState.DISCONNECTED)
                self._schedule_reconnect()
                return

            self.websocket_inst = websocket_inst

            # 控制recv调用阻塞的时间
            websocket_inst.settimeout(1)

            self.channel_recv_thread = threading.Thread(
                target=self._channel_recv_thread_run, daemon=True,
                name="%s_%s" % (self.tag, "channel_recv_thread"))

            self.channel_send_thread = threading.Thread(
                target=self._channel_send_thread_run, daemon=True,
                name="%s_%s" % (self.tag, "channel_send_thread"))

            self.set_channel_connect_state(GeneralConnectState.CONNECTED)
            self.set_client_connect_state(GeneralConnectState.CONNECTED)

            self.channel_recv_thread.start()
            self.channel_send_thread.start()

            self._reset_reconnect_data()

        if self.on_open:
            self.on_open(connect_session)

    def _schedule_reconnect(self):
        log_prefix = f"{self.log_prefix} _schedule_reconnect:"

        reconnect_interval = self.reconnect_backoff_seq[self.reconnect_backoff_idx] * \
                             self.reconnect_base_interval

        if not self.reconnect_backoff_at_max:
            self.reconnect_backoff_idx += 1

        self.logger.info(f"{log_prefix} connect_trial_cnt {self.connect_trial_cnt} "
                         f"reconnect after {reconnect_interval}, "
                         f"next reconnect_backoff_idx {self.reconnect_backoff_idx}")

        self.reconnect_timer = threading.Timer(
            reconnect_interval, self._do_connect, args=[self.connect_session])
        self.reconnect_timer.start()

    def _do_channel_disconnect(self):
        log_prefix = "%s _do_channel_disconnect:" % (self.log_prefix,)

        self.set_channel_connect_state(GeneralConnectState.DISCONNECTING)

        with self.channel_send_list_condition:
            self.channel_send_list_condition.notify()

        final_time = datetime.utcnow() + timedelta(seconds=10)

        while datetime.utcnow() <= final_time:
            if self.channel_send_thread is not None or self.channel_recv_thread is not None:
                time.sleep(0.02)
                continue

            self.set_channel_connect_state(GeneralConnectState.DISCONNECTED)

            self._on_channel_disconnected()

            return

        err_msg = "%s Timeout, channel_send_thread %s, channel_recv_thread %s" % (
            log_prefix, self.channel_send_thread, self.channel_recv_thread)

        self.logger.error(err_msg)

        sys.exit(err_msg)

    def _on_channel_disconnected(self):
        connect_session = self.connect_session

        if self.is_client_connected() and self.auto_reconnect:
            self.set_client_connect_state(GeneralConnectState.CONNECTING)
            self._schedule_reconnect()
        else:
            self.connect_session = None
            self.set_client_connect_state(GeneralConnectState.DISCONNECTED)

        if self.on_close:
            threading.Timer(0, self.on_close, args=[connect_session]).start()

    # region Send And Recv Thread Handling
    def _channel_recv_thread_run(self):
        log_prefix = "%s _channel_recv_thread_run:" % (self.log_prefix,)

        self.logger.info("%s THREAD Start" % log_prefix)

        self._iter_channel_msg_recv()
        self.channel_recv_thread = None

        self.logger.info("%s THREAD End" % log_prefix)

        self._on_channel_recv_end()

    def _iter_channel_msg_recv(self):
        log_prefix = "%s _iter_channel_msg_recv:" % (self.log_prefix,)

        try:
            while self.is_channel_connected():
                try:
                    _, recv_data = self.websocket_inst.recv_data()
                except websocket.WebSocketTimeoutException as e:
                    continue

                if not recv_data:
                    self.logger.info("%s, recv_data: %s" % (self.log_prefix, recv_data))
                    continue

                if self.on_message:
                    threading.Timer(0, self.on_message, args=[recv_data]).start()
        except Exception as e:
            if self.is_channel_connected():
                if not self.channel_end_expected:
                    self.logger.error("%s Exception %s" % (log_prefix, str(e)))
                else:
                    self.logger.info("%s Exception %s" % (log_prefix, str(e)))

        self.logger.info("%s Leave with _channel_connect_state %s" % (
            log_prefix, self._channel_connect_state))

    def _channel_send_thread_run(self):
        log_prefix = "%s _channel_send_thread_run:" % (self.log_prefix,)

        self.logger.info("%s THREAD Start" % log_prefix)

        self._iter_channel_msg_send()
        self.channel_send_thread = None

        self.logger.info("%s THREAD End" % log_prefix)

        self._on_channel_send_end()

    def _iter_channel_msg_send(self):
        log_prefix = "%s _iter_channel_msg_send:" % (self.log_prefix,)

        try:
            while self.is_channel_connected():
                with self.channel_send_list_condition:
                    if self.channel_send_list:
                        first_msg, first_msg_id = self.channel_send_list.pop(0)
                    else:
                        self.channel_send_list_condition.wait(100)
                        continue

                if first_msg is not None:
                    self.logger.info("%s to send msg with msg_id %s" % (
                        log_prefix, first_msg_id))

                    self.websocket_inst.send(first_msg)
                else:
                    self.logger.info("%s received stop msg" % (log_prefix,))
                    break
        except Exception as e:
            if self.is_channel_connected():
                self.logger.error("%s Exception %s" % (log_prefix, str(e)))

        self.logger.info("%s Leave with _channel_connect_state %s" % (
            log_prefix, self._channel_connect_state))

    def _on_channel_recv_end(self):
        self._on_channel_connection_end()

    def _on_channel_send_end(self):
        self._on_channel_connection_end()

    def _on_channel_connection_end(self):
        with self.lock:
            if self.is_channel_connected():
                self._do_channel_disconnect()
    # endregion
