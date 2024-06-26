import logging
import threading
import time
from datetime import datetime, timedelta
import ssl
import sys
import websocket

from zhiyoufy.common.utils import LogUtil, StrUtil, RandomUtil
from zhiyoufy.common.base_signal_handler_runnable import BaseSignalHandlerRunnable
from zhiyoufy.common.handle import BaseInRequestHandler, BaseOutRequestHandler, ReqSrc
from zhiyoufy.common.models import BaseEvent, WaitResponseTimerEvent, BaseResultErrorType
from zhiyoufy.stomper import StompDecoder, BufferingStompDecoder, Frame

from .channel_event import MasterChannelEventType
from .channel_signal import MasterChannelSignal
from .connect_state import MasterConnectState


class MasterChannelController(BaseSignalHandlerRunnable):
    HEARTBEAT_MSG = b"\n"

    def __init__(self):
        super().__init__()

        self.logger = logging.getLogger("zhiyoufy_worker.MasterChannelController")

        self.controller_started = False
        self.async_signals = [
            MasterChannelSignal.TO_MASTER_CHANNEL_EVENT
        ]

        self.master_channel = None
        self._master_connect_state = MasterConnectState.DISCONNECTED
        self.stop_connect_master = False
        self.need_report_disconnected = False
        self.need_reconnect = False

        self.master_channel_connect_timeout = 6
        self.master_channel_connect_timer = None
        self.active_connect_timer_event = None

        # recv direction
        self.master_channel_recv_thread = None
        self.stomp_decoder = None
        self.buffering_stomp_decoder = None

        # send direction
        self.master_channel_send_thread = None
        self.master_channel_send_list = []
        self.master_channel_send_list_condition = threading.Condition()

        # heartbeat related
        self.default_heart_beat = (20_000, 20_000)
        self.last_recv_msg_time = None
        self.recv_heartbeat_period = None
        self.next_send_heartbeat_time = None
        self.send_heartbeat_period = None

        # reconnect related
        self.connect_trial_cnt = 0
        self.reconnect_base_interval = 2
        self.reconnect_backoff_idx = 0
        self.reconnect_backoff_seq = [1, 1, 1, 4, 8, 16, 32, 60]
        self.reconnect_to_master_timer = None
        self.last_connected_to_master_time = None
        self.last_connect_to_master_time = None
        if "reconnect_backoff_at_max_log_interval" in self.config_inst:
            self.reconnect_backoff_at_max_log_interval = self.config_inst.reconnect_backoff_at_max_log_interval
        else:
            self.reconnect_backoff_at_max_log_interval = 1800

        self._init_module_config()

    def _init_module_config(self):
        config_inst = self.config_inst

        if "master_channel_websocket_connect_timeout" in config_inst:
            self.master_channel_websocket_connect_timeout = \
                config_inst.master_channel_websocket_connect_timeout
        else:
            self.master_channel_websocket_connect_timeout = 5

    @property
    def master_addr_full(self):
        return self.config_inst.master_addr + "/zhiyoufy-api/v1/websocket-stomp"

    @property
    def master_connect_state(self):
        return self._master_connect_state

    @master_connect_state.setter
    def master_connect_state(self, new_state):
        if self._master_connect_state != new_state:
            self.logger.info("master_connect_state changes from %s to %s" % (
                self._master_connect_state, new_state))
            self._master_connect_state = new_state

            if self._master_connect_state == MasterConnectState.CONNECTED:
                event = BaseEvent(event_type=MasterChannelEventType.MASTER_CHANNEL_CONNECTED)
                MasterChannelSignal.FROM_MASTER_CHANNEL_EVENT.send(
                    type(self).__name__, event=event)

                self.reset_reconnect_data()

                self.need_report_disconnected = True
            elif self._master_connect_state == MasterConnectState.DISCONNECTED:
                self.master_channel_send_list = []

                if self.master_channel:
                    self.master_channel.close()
                    self.master_channel = None

                if self.need_report_disconnected:
                    self.need_report_disconnected = False

                    event = BaseEvent(event_type=MasterChannelEventType.MASTER_CHANNEL_DISCONNECTED)
                    MasterChannelSignal.FROM_MASTER_CHANNEL_EVENT.send(
                        type(self).__name__, event=event)

    @property
    def reconnect_backoff_at_max(self):
        if self.reconnect_backoff_idx == (len(self.reconnect_backoff_seq) - 1):
            return True
        else:
            return False

    def reset_reconnect_data(self):
        self.connect_trial_cnt = 0
        self.reconnect_backoff_idx = 0

    def start(self):
        log_prefix = "%s start:" % (self.log_prefix,)

        self.logger.info("%s Enter" % log_prefix)

        if self.controller_started:
            raise Exception("%s already started" % log_prefix)

        self.controller_started = True

        self.start_handler_and_connect_signals()

        self.logger.info("%s Leave" % log_prefix)

    def stop(self):
        log_prefix = "%s stop:" % (self.log_prefix,)

        self.logger.info("%s Enter" % log_prefix)

        if not self.controller_started:
            raise Exception("%s not started" % log_prefix)

        self._disconnect_from_master()

        self.disconnect_signals_and_stop_handler()

        self.controller_started = False

        self.logger.info("%s Leave" % log_prefix)

    def is_master_connected(self):
        if self.master_connect_state == MasterConnectState.CONNECTED:
            return True
        return False

    def is_master_connecting(self):
        if self.master_connect_state == MasterConnectState.CONNECTING:
            return True
        return False

    def is_master_disconnecting(self):
        if self.master_connect_state == MasterConnectState.DISCONNECTING:
            return True
        return False

    def is_master_disconnected(self):
        if self.master_connect_state == MasterConnectState.DISCONNECTED:
            return True
        return False

    def on_event(self, event):
        log_prefix = "%s on_event:" % (self.log_prefix,)

        try:
            if event.event_type == MasterChannelEventType.MASTER_CHANNEL_CONNECT_REQ:
                self._on_master_channel_connect_req()
            elif event.event_type == MasterChannelEventType.MASTER_CHANNEL_CONNECT_TIMEOUT:
                self._on_event_master_channel_connect_timeout()
            elif event.event_type == MasterChannelEventType.MASTER_CHANNEL_RECONNECT_TIMEOUT:
                self._on_event_master_channel_reconnect_timeout()
            elif event.event_type == MasterChannelEventType.MASTER_CHANNEL_RECV_END:
                self._on_master_channel_recv_end()
            elif event.event_type == MasterChannelEventType.MASTER_CHANNEL_SEND_END:
                self._on_master_channel_send_end(event)
            elif event.event_type == MasterChannelEventType.FROM_MASTER_STOMP_MESSAGE:
                self._on_from_master_stomp_message(event)
            elif event.event_type == MasterChannelEventType.TO_MASTER_STOMP_MESSAGE:
                self._on_to_master_stomp_message(event)
            else:
                raise Exception("Unknown event_type %s" % event.event_type)
        except Exception as e:
            self.logger.error("%s met Exception %s" % (log_prefix, str(e)))

    def _connect_to_master(self):
        config_inst = self.config_inst

        log_prefix = f"{self.log_prefix} connect_to_master {config_inst.master_addr}:"

        self.logger.info(f"{log_prefix} Enter")

        if not self.is_master_disconnected():
            err_msg = f"{log_prefix} Invalid state {self.master_connect_state}"
            self.logger.error(err_msg)
            sys.exit(err_msg)

        self.master_connect_state = MasterConnectState.CONNECTING
        self.connect_trial_cnt += 1

        try:
            self.master_channel = websocket.create_connection(
                self.master_addr_full,
                timeout=self.master_channel_websocket_connect_timeout,
                enable_multithread=True,
                subprotocols=["v12.stomp"],
                sslopt={"cert_reqs": ssl.CERT_NONE})
        except Exception as ex:
            self.logger.info(f"{log_prefix} create_connection met exception {ex}")
            self.master_connect_state = MasterConnectState.DISCONNECTED
            self._schedule_reconnect_to_master()
            return

        # 控制recv调用阻塞的时间
        self.master_channel.settimeout(1)

        self.stomp_decoder = StompDecoder()
        self.buffering_stomp_decoder = BufferingStompDecoder(self.stomp_decoder)

        self.last_recv_msg_time = None
        self.recv_heartbeat_period = None
        self.send_heartbeat_period = None
        self.next_send_heartbeat_time = None

        self.master_channel_recv_thread = threading.Thread(
            target=self._master_channel_recv_thread_run, daemon=True,
            name="%s_%s" % (self.tag, "master_channel_recv_thread"))
        self.master_channel_recv_thread.start()

        self.master_channel_send_thread = threading.Thread(
            target=self._master_channel_send_thread_run, daemon=True,
            name="%s_%s" % (self.tag, "master_channel_send_thread"))
        self.master_channel_send_thread.start()

        stomp_msg = Frame()
        stomp_msg.cmd = "CONNECT"
        stomp_msg.headers = {
            "accept-version": "1.2",
            "host": config_inst.master_host,
            "heart-beat": f"{self.default_heart_beat[0]},{self.default_heart_beat[1]}",
        }
        stomp_msg_packed = stomp_msg.pack()
        msg_id = RandomUtil.gen_guid()

        self.logger.info(f"{log_prefix} send connect_req msg_id {msg_id} {stomp_msg_packed}")

        self._send_msg_to_master(stomp_msg_packed, msg_id)

        self.master_channel_connect_timer = threading.Timer(
            self.master_channel_connect_timeout, self._on_master_channel_connect_timeout)
        self.master_channel_connect_timer.start()

        outbound_req_handler = BaseOutRequestHandler(
            log_prefix, self.logger,
            elk_record_type=MasterChannelEventType.TO_MASTER_STOMP_CONNECT_REQ,
            req_src=ReqSrc.WebSocket,
            req_msg=stomp_msg.to_json(),
            guid=msg_id,
        )

        self.active_connect_timer_event = WaitResponseTimerEvent(
            req_handler=outbound_req_handler,
            guid=msg_id
        )

    def _end_to_master_stomp_connect_req(self, err_type, status_code=400):
        timer_event = self.active_connect_timer_event
        self.active_connect_timer_event = None

        if not timer_event:
            return

        req_handler = timer_event.req_handler
        req_handler.build_base_response(err_type, status_code)

    def _on_master_channel_connect_timeout(self):
        self._cancel_master_channel_connect_timer()

        self.send_simple_event_to_handler(MasterChannelEventType.MASTER_CHANNEL_CONNECT_TIMEOUT)

    def _cancel_master_channel_connect_timer(self):
        if self.master_channel_connect_timer:
            self.master_channel_connect_timer.cancel()
            self.master_channel_connect_timer = None

    def _disconnect_from_master(self, timeout=10):
        log_prefix = "%s disconnect_from_master" % (self.log_prefix,),

        if self.is_master_disconnected():
            return

        self._end_to_master_stomp_connect_req(BaseResultErrorType.RES_ERR_CANCELLED)
        self._cancel_master_channel_connect_timer()

        self.master_connect_state = MasterConnectState.DISCONNECTING
        self.stop_connect_master = True

        with self.master_channel_send_list_condition:
            self.master_channel_send_list_condition.notify()

        final_time = datetime.utcnow() + timedelta(seconds=timeout)

        while datetime.utcnow() <= final_time:
            if self.master_channel_send_thread is not None or \
                    self.master_channel_recv_thread is not None:
                time.sleep(0.02)
                continue

            self.stop_connect_master = False
            self.master_connect_state = MasterConnectState.DISCONNECTED

            return

        err_msg = "%s Timeout, master_channel_send_thread %s, master_channel_recv_thread %s" % (
            log_prefix, self.master_channel_send_thread, self.master_channel_recv_thread)

        self.logger.error(err_msg)

        sys.exit(err_msg)

    def _cancel_reconnect_to_master_timer(self):
        if self.reconnect_to_master_timer is not None:
            self.reconnect_to_master_timer.cancel()
            self.reconnect_to_master_timer = None

    def _disconnect_then_reconnect(self):
        self._disconnect_from_master()

        self._schedule_reconnect_to_master()

    def _schedule_reconnect_to_master(self):
        log_prefix = f"{self.log_prefix} schedule_reconnect_to_master:"

        connect_interval = self.reconnect_backoff_seq[self.reconnect_backoff_idx] * self.reconnect_base_interval

        if not self.reconnect_backoff_at_max:
            self.reconnect_backoff_idx += 1

        self.logger.info(f"{log_prefix} connect_trial_cnt {self.connect_trial_cnt} "
                         f"reconnect after {connect_interval}, "
                         f"next reconnect_backoff_idx {self.reconnect_backoff_idx}")

        self.reconnect_to_master_timer = threading.Timer(connect_interval, self._on_reconnect_to_master_timeout)
        self.reconnect_to_master_timer.start()

    def _on_reconnect_to_master_timeout(self):
        self.send_simple_event_to_handler(MasterChannelEventType.MASTER_CHANNEL_RECONNECT_TIMEOUT)

    # region Event Handling
    def _on_master_channel_connect_req(self):
        event_handler = BaseInRequestHandler(
            log_prefix="%s on_master_channel_connect_req" % (self.log_prefix,),
            logger_name=self.logger,
            elk_record_type=MasterChannelEventType.MASTER_CHANNEL_CONNECT_REQ,
            req_src=ReqSrc.LocalEvent
        )

        try:
            self.need_reconnect = True
            self.connect_trial_cnt = 0
            self._connect_to_master()
            event_handler.build_base_response(BaseResultErrorType.RES_OK)
        except Exception as e:
            LogUtil.log_event_with_err_msg(event_handler, e)

    def _on_event_master_channel_connect_timeout(self):
        event_handler = BaseInRequestHandler(
            log_prefix="%s on_event_master_channel_connect_timeout" % (self.log_prefix,),
            logger_name=self.logger,
            elk_record_type=MasterChannelEventType.MASTER_CHANNEL_CONNECT_TIMEOUT,
            req_src=ReqSrc.LocalEvent
        )

        event_handler.logger.info("%s Enter" % (event_handler.log_prefix,))

        try:
            if self.is_master_connecting():
                self._end_to_master_stomp_connect_req(BaseResultErrorType.RES_ERR_TIMEOUT)
                self._cancel_master_channel_connect_timer()

                self._disconnect_from_master()

                if self.need_reconnect:
                    self._schedule_reconnect_to_master()

            event_handler.build_base_response(BaseResultErrorType.RES_OK)
        except Exception as e:
            LogUtil.log_event_with_err_msg(event_handler, e)

    def _on_event_master_channel_reconnect_timeout(self):
        event_handler = BaseInRequestHandler(
            log_prefix="%s on_event_master_channel_reconnect_timeout" % (self.log_prefix,),
            logger_name=self.logger,
            elk_record_type=MasterChannelEventType.MASTER_CHANNEL_RECONNECT_TIMEOUT,
            req_src=ReqSrc.LocalEvent
        )

        event_handler.logger.info("%s Enter" % (event_handler.log_prefix,))

        try:
            self._connect_to_master()

            event_handler.build_base_response(BaseResultErrorType.RES_OK)
        except Exception as e:
            LogUtil.log_event_with_err_msg(event_handler, e)

    def _on_master_channel_recv_end(self):
        self._on_master_channel_connection_end()

    def _on_master_channel_send_end(self, event):
        self._on_master_channel_connection_end()

    def _on_master_channel_connection_end(self):
        if self.active_connect_timer_event:
            self._end_to_master_stomp_connect_req(BaseResultErrorType.RES_ERR_CONNECTION_END)
            self._cancel_master_channel_connect_timer()

        if self.is_master_connected() or self.is_master_connecting():
            self._disconnect_from_master()

            if self.need_reconnect:
                self._schedule_reconnect_to_master()

    def _on_from_master_stomp_message(self, event):
        log_prefix = "%s _on_from_master_stomp_message:" % (self.log_prefix,)

        self.logger.info("%s recv event %s" % (log_prefix, event))

        stomp_msg = event.content["stomp_msg"]

        if stomp_msg["cmd"] == "CONNECTED":
            self._on_from_master_stomp_message_connected(event, stomp_msg)
        else:
            MasterChannelSignal.FROM_MASTER_CHANNEL_EVENT.send(
                self, event=event)

    def _on_from_master_stomp_message_connected(self, event, stomp_msg):
        log_prefix = "%s _on_from_master_stomp_message_connected:" % (self.log_prefix,)

        timer_event = self.active_connect_timer_event
        self.active_connect_timer_event = None

        if not timer_event:
            self.logger.error("%s no pending connect req" % (log_prefix,))

            self._disconnect_then_reconnect()

            return

        req_handler = timer_event.req_handler
        dumped_response = stomp_msg
        req_handler.log_response_with_elk(dumped_response)

        self._cancel_master_channel_connect_timer()

        server_heart_beat = stomp_msg["headers"].get("heart-beat", None)
        if server_heart_beat:
            sx = int(server_heart_beat.split(",")[0])
            sy = int(server_heart_beat.split(",")[1])
            cx = self.default_heart_beat[0]
            cy = self.default_heart_beat[1]
            c2s = max(cx, sy)
            if c2s > 0:
                self.send_heartbeat_period = float(c2s)/1000 - 3
                self.next_send_heartbeat_time = datetime.utcnow() + timedelta(seconds=self.send_heartbeat_period)
            s2c = max(cy, sx)
            if s2c > 0:
                self.recv_heartbeat_period = float(s2c)/1000 + 10

        self.master_connect_state = MasterConnectState.CONNECTED

        self.logger.info("%s Leave connect ok" % log_prefix)

    def _on_to_master_stomp_message(self, event):
        self._send_msg_to_master(msg=event.content_extra["stomp_msg"], msg_id=event.content["msg_id"])
    # endregion

    # region Send And Recv Thread Handling
    def _master_channel_recv_thread_run(self):
        log_prefix = "%s master_channel_recv_thread_run:" % (self.log_prefix,)

        self.logger.info("%s THREAD Start" % log_prefix)

        self._iter_master_channel_msg_recv()
        self.master_channel_recv_thread = None

        self.logger.info("%s THREAD End" % log_prefix)

    def _iter_master_channel_msg_recv(self):
        log_prefix = "%s iter_master_channel_msg_recv:" % (self.log_prefix,)

        try:
            while not self.stop_connect_master:
                self._check_recv_heartbeat()
                self._send_heartbeat_if_needed()

                try:
                    _, recv_data = self.master_channel.recv_data()
                except websocket.WebSocketTimeoutException as e:
                    continue

                if not recv_data:
                    self.logger.info("%s, recv_data: %s" % (self.log_prefix, recv_data))
                    continue

                self.last_recv_msg_time = datetime.utcnow()

                if recv_data == self.HEARTBEAT_MSG:
                    self.logger.debug(f"{self.log_prefix}, recv HEARTBEAT_MSG")
                    continue

                self.logger.info("%s, recv_data: %s" % (self.log_prefix, recv_data))

                messages = self.buffering_stomp_decoder.decode(recv_data)

                for message in messages:
                    self.logger.info(f"{log_prefix} recv msg {StrUtil.pprint(message)}")
                    event = BaseEvent(event_type=MasterChannelEventType.FROM_MASTER_STOMP_MESSAGE)
                    event.content["stomp_msg"] = message

                    self.send_event_to_handler(event)
        except Exception as e:
            if not self.stop_connect_master:
                self.logger.error("%s Exception %s" % (log_prefix, str(e)))

        if not self.is_master_disconnecting():
            self.send_simple_event_to_handler(MasterChannelEventType.MASTER_CHANNEL_RECV_END)

    def _check_recv_heartbeat(self):
        log_prefix = f"{self.log_prefix} _check_recv_heartbeat:"

        if not self.recv_heartbeat_period:
            return
        if datetime.utcnow() > (self.last_recv_msg_time + timedelta(seconds=self.recv_heartbeat_period)):
            err_msg = f"{log_prefix} timeout without recv heartbeat"
            self.logger.error(err_msg)
            raise Exception(err_msg)

    def _master_channel_send_thread_run(self):
        log_prefix = "%s _master_channel_send_thread_run:" % (self.log_prefix,)

        self.logger.info("%s THREAD Start" % log_prefix)

        self._iter_master_channel_msg_send()
        self.master_channel_send_thread = None

        self.logger.info("%s THREAD End" % log_prefix)

    def _iter_master_channel_msg_send(self):
        log_prefix = "%s _iter_master_channel_msg_send:" % (self.log_prefix,)

        last_log_time = datetime.utcnow()

        try:
            while not self.stop_connect_master:
                with self.master_channel_send_list_condition:
                    if self.master_channel_send_list:
                        first_msg, first_msg_id = self.master_channel_send_list.pop(0)
                    else:
                        if (datetime.utcnow() - last_log_time) > timedelta(seconds=self.alive_log_period):
                            self.logger.info("%s alive" % log_prefix)
                            last_log_time = datetime.utcnow()
                        self.master_channel_send_list_condition.wait(100)
                        continue

                if first_msg:
                    self.logger.info("%s to send msg with msg_id %s" % (
                        log_prefix, first_msg_id))

                    self.master_channel.send(first_msg)
                else:
                    self.logger.info("%s received stop msg %s" % (log_prefix, first_msg))
                    break
        except Exception as e:
            if not self.stop_connect_master:
                self.logger.error("%s Exception %s" % (log_prefix, str(e)))

        if not self.is_master_disconnecting():
            self.send_simple_event_to_handler(MasterChannelEventType.MASTER_CHANNEL_SEND_END)

        self.logger.info("%s Leave with stop_connect_master %s" % (log_prefix, self.stop_connect_master))

    def _send_heartbeat_if_needed(self):
        if self.next_send_heartbeat_time is None:
            return

        time_diff = self.next_send_heartbeat_time - datetime.utcnow()

        if timedelta(seconds=1) < time_diff < timedelta(seconds=self.send_heartbeat_period+10):
            return

        self.logger.debug(f"{self.log_prefix} to send HEARTBEAT_MSG")
        self.master_channel.send(self.HEARTBEAT_MSG)

        self.next_send_heartbeat_time = datetime.utcnow() + timedelta(seconds=self.send_heartbeat_period)

    def _send_msg_to_master(self, msg, msg_id):
        with self.master_channel_send_list_condition:
            self.master_channel_send_list.append((msg, msg_id))
            self.master_channel_send_list_condition.notify()
    # endregion

