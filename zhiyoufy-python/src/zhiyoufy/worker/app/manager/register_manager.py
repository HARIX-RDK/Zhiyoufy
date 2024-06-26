import threading
import json

from zhiyoufy.common.handle import BaseOutRequestHandler, ReqSrc
from zhiyoufy.common.utils import RandomUtil

from zhiyoufy.stomper import Frame

from zhiyoufy.worker.app.worker_app_bridge import WorkerAppBridge
from zhiyoufy.worker.app.worker_app_event import WorkerAppEventType
from zhiyoufy.worker.app.worker_state_key import WorkerStateKey


class RegisterManager(WorkerAppBridge):
    def __init__(self, **kwargs):
        super().__init__(**kwargs)

        self.tag = type(self).__name__
        self.log_prefix = f"{self.tag}:"

        self.register_timeout = 20
        self.master_register_timer = None
        self.register_req_handler = None

    def on_master_channel_connected(self):
        self.register_to_master()

    def on_master_channel_disconnected(self):
        self.set_state(WorkerStateKey.REGISTRATION, False)

    def register_to_master(self):
        log_prefix = "%s register_to_master:" % self.log_prefix

        self.dump_state_store()

        worker_app_config = self.config_inst.worker_app

        destination = "/app/worker-register"
        msg_id = RandomUtil.gen_guid()

        active_jobs = self.active_jobs

        body = {
            "workerApp": worker_app_config.worker_app_name,
            "workerGroup": worker_app_config.worker_group,
            "groupTokenName": worker_app_config.group_token_name,
            "groupTokenSecret": worker_app_config.group_token_secret,

            "appRunId": self.global_context.app_run_id,
            "appStartTimestamp": self.global_context.app_start_timestamp,

            "workerName": worker_app_config.worker_name,
            "maxActiveJobNum": worker_app_config.max_active_job_num,
            "activeJobs": active_jobs,
        }

        stomp_msg_frame = Frame()
        stomp_msg_frame.cmd = "SEND"
        stomp_msg_frame.headers = {
            "destination": destination,
        }
        stomp_msg_frame.body = body

        stomp_msg_json = stomp_msg_frame.to_json()
        stomp_msg_binary = stomp_msg_frame.pack_json_binary()

        outbound_req_handler = BaseOutRequestHandler(
            log_prefix, self.logger,
            elk_record_type=WorkerAppEventType.OUT_WORKER_REGISTER_REQ,
            req_src=ReqSrc.LocalEvent,
            req_msg=stomp_msg_json,
            guid=msg_id,
        )

        self.register_req_handler = outbound_req_handler

        self.logger.info("%s send register_req with msg_id %s" % (log_prefix, msg_id))

        self.send_msg_to_master(stomp_msg_binary, msg_id, description=destination)

        self.master_register_timer = threading.Timer(self.register_timeout, self._on_timeout_master_register_timer)
        self.master_register_timer.start()

    def _on_timeout_master_register_timer(self):
        self.logger.info("%s _on_timeout_master_register_timer" % self.log_prefix)

        self.send_simple_event_to_handler(WorkerAppEventType.MASTER_REGISTER_TIMEOUT)

    def _cancel_register_timer(self):
        if self.master_register_timer is not None:
            self.master_register_timer.cancel()
            self.master_register_timer = None

    def on_register_rsp(self, stomp_msg):
        log_prefix = f"{self.log_prefix} on_register_rsp:"

        if not self.register_req_handler:
            self.logger.error(f"{log_prefix} no pending register req handler")
            return

        self.register_req_handler.log_response_with_elk(stomp_msg)
        self.register_req_handler = None

        self._cancel_register_timer()

        rsp_json = json.loads(stomp_msg["body"])

        if "error" in rsp_json:
            self.logger.error("%s register failed" % (log_prefix,))

            return

        self.set_state(WorkerStateKey.REGISTRATION, True)

        self.logger.info("%s register ok" % log_prefix)

