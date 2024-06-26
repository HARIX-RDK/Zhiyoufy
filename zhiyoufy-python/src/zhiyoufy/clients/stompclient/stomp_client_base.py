import json
import requests
import threading
import time
from datetime import datetime, timedelta

from zhiyoufy.common.utils import CheckUtil, LogUtil, StrUtil
from zhiyoufy.common import GeneralConnectState
from zhiyoufy.websocket.websocket_client import WebsocketClient

from .stomp_client_bridge import StompClientBridge
from .activation_state import ActivationState
from .stomp_handler import StompHandler


class StompClientBase(StompClientBridge):
    def __init__(self, parent_client):
        super().__init__(parent_client=parent_client)

        self.logger = self.robot_logger
        self.broker_url = None

        self.connect_headers = {
            "login": "guest",
            "passcode": "guest",
        }
        self.disconnect_headers = {}

        self.activation_state = ActivationState.INACTIVE
        self.stomp_handler = None
        self.websocket_client = None

    @property
    def active(self):
        return self.activation_state == ActivationState.ACTIVE

    @property
    def stomp_connected(self):
        stomp_handler = self.stomp_handler
        return stomp_handler and stomp_handler.connected

    def base_activate(self):
        log_prefix = f"{self.log_prefix} base_activate:"

        self.logger.info(f"{log_prefix} Enter")

        try:
            if self.activation_state == ActivationState.DEACTIVATING:
                raise Exception("Still DEACTIVATING, can not activate now")

            if self.active:
                self.logger.info(f"{log_prefix} Already ACTIVE, ignoring request to activate")
                return

            self._change_activation_state(ActivationState.ACTIVE)

            self._connect()
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def base_configure(self, params):
        log_prefix = f"{self.log_prefix} base_configure:"

        self.logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            self.broker_url = params["broker_url"]

            for attr_name in ["connect_headers"]:
                if attr_name in params:
                    setattr(self, attr_name, params[attr_name])
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def base_deactivate(self):
        log_prefix = f"{self.log_prefix} base_deactivate:"

        self.logger.info(f"{log_prefix} Enter")

        try:
            if self.activation_state != ActivationState.ACTIVE:
                raise Exception(f"{log_prefix} invalid state {self.activation_state}")

            self._change_activation_state(ActivationState.DEACTIVATING)
            self.websocket_client.auto_reconnect = False
            self.websocket_client.channel_end_expected = True

            if not self.stomp_handler:
                self.websocket_client.disconnect()
                self._change_activation_state(ActivationState.INACTIVE)
            else:
                self.stomp_handler.dispose()
                self.stomp_handler = None
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def base_publish(self, params):
        log_prefix = f"{self.log_prefix} base_publish:"

        self.logger.info(f"{log_prefix} Enter with params {StrUtil.pprint(params)}")

        json_body = params.get("json_body")

        if json_body:
            params["body"] = json.dumps(json_body)

        self.stomp_handler.publish(params)

    def base_subscribe(self, destination, callback, headers=None):
        if headers is None:
            headers = {}
        self.stomp_handler.subscribe(destination, callback, headers)

    def base_unsubscribe(self, id, headers=None):
        if headers is None:
            headers = {}
        self.stomp_handler.subscribe(id, headers)

    def base_wait_inactive(self, params):
        log_prefix = f"{self.log_prefix} base_wait_inactive:"

        self.logger.info(f"{log_prefix} Enter with params {StrUtil.pprint(params)}")

        timeout = params["timeout"]

        final_time = datetime.now() + timedelta(seconds=timeout)

        while datetime.now() <= final_time:
            left_time = (final_time - datetime.now()).total_seconds()
            if left_time < 0:
                break

            if not self.activation_state == ActivationState.INACTIVE:
                time.sleep(0.02)
                continue

            return True

        return False

    def base_wait_stomp_connected(self, params):
        log_prefix = f"{self.log_prefix} base_wait_stomp_connected:"

        self.logger.info(f"{log_prefix} Enter with params {StrUtil.pprint(params)}")

        timeout = params["timeout"]

        final_time = datetime.now() + timedelta(seconds=timeout)

        while datetime.now() <= final_time:
            left_time = (final_time - datetime.now()).total_seconds()
            if left_time < 0:
                break

            if not self.stomp_connected:
                time.sleep(0.02)
                continue

            return True

        return False

    def check_stomp_connected(self):
        if not self.stomp_connected:
            raise Exception("Not stomp_connected yet??")

    def on_event(self, event):
        pass
    
    def _change_activation_state(self, new_state):
        log_prefix = f"{self.log_prefix} _change_activation_state:"

        if new_state == self.activation_state:
            return

        self.logger.info(f"{log_prefix} from {self.activation_state} to {new_state}")

        self.activation_state = new_state

    def _connect(self):
        log_prefix = f"{self.log_prefix} _connect:"

        self.logger.info(f"{log_prefix} Enter")

        self.logger.info(f"{log_prefix} Opening Web Socket...")

        self.websocket_client = WebsocketClient(
            server_url=self.broker_url, subprotocols=["v12.stomp"],
            on_open=self._on_websocket_open, on_close=self._on_websocket_close
        )

        self.websocket_client.connect()

    def _on_websocket_open(self, connect_session):
        log_prefix = f"{self.log_prefix} _on_websocket_open:"

        self.logger.info(f"{log_prefix} Enter")

        stomp_handler = StompHandler(parent_client=self.parent_client)
        self.stomp_handler = stomp_handler

        stomp_handler.set_websocket_client(self.websocket_client)

        stomp_handler.on_websocket_open(connect_session)

    def _on_websocket_close(self, connect_session):
        log_prefix = f"{self.log_prefix} _on_websocket_close:"

        self.logger.info(f"{log_prefix} Enter")

        if self.stomp_handler:
            self.stomp_handler.dispose()
            self.stomp_handler = None

        if self.activation_state == ActivationState.DEACTIVATING:
            self._change_activation_state(ActivationState.INACTIVE)
