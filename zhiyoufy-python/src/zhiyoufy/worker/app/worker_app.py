import logging
import threading
import os
import traceback
from datetime import datetime
from enum import Enum

from zhiyoufy.common import config as _config
from zhiyoufy.common.base_signal_handler_runnable import BaseSignalHandlerRunnable
from zhiyoufy.common.handle import BaseInRequestHandler, ReqSrc
from zhiyoufy.common.models import BaseEvent
from zhiyoufy.common.utils import IOUtil

from zhiyoufy.worker.master import MasterChannelSignal, MasterChannelEventType
from zhiyoufy.worker.app.handler import WorkerAppHandlerApp, WorkerAppHandlerMasterChannel
from zhiyoufy.worker.app.manager import JobManager, RegisterManager

from .worker_app_event import WorkerAppEventType
from .worker_state_key import WorkerStateKey


class WorkerApp(BaseSignalHandlerRunnable):
    def __init__(self):
        super().__init__()

        self.logger = logging.getLogger("zhiyoufy_worker.WorkerApp")

        self.app_started = False

        self.async_signals = [
            MasterChannelSignal.FROM_MASTER_CHANNEL_EVENT
        ]

        self.job_manager = JobManager(parent_app=self)
        self.register_manager = RegisterManager(parent_app=self)

        self.handler_app = WorkerAppHandlerApp(parent_app=self)
        self.handler_master_channel = WorkerAppHandlerMasterChannel(parent_app=self)

        self.state_store = {}
        self.set_state(WorkerStateKey.REGISTRATION, False)
        self.set_state(WorkerStateKey.ACTIVE_JOBS, {})
        self.set_state(WorkerStateKey.ACTIVE_JOB_RESULT_INDS, [])

        self.dump_state_timer = None

        self.job_runners = {}

        self._init_module_config()

    @property
    def worker_app_config(self):
        return self.config_inst.worker_app

    def start(self):
        log_prefix = "%s start:" % (self.log_prefix,)

        self.logger.info("%s Enter" % log_prefix)

        if self.app_started:
            raise Exception("%s already started" % log_prefix)

        self.app_started = True

        self.start_handler_and_connect_signals()

        self.dump_state_timer = threading.Timer(self.worker_app_config.dump_state_store_timeout,
                                                self._on_timeout_dump_state)
        self.dump_state_timer.start()

        self.logger.info("%s Leave" % log_prefix)

    def stop(self):
        log_prefix = "%s stop:" % (self.log_prefix,)

        if not self.app_started:
            self.logger.error("%s ignore, not started yet" % log_prefix)
            return

        if self.dump_state_timer:
            self.dump_state_timer.cancel()
            self.dump_state_timer = None

        self.disconnect_signals_and_stop_handler()

        self.app_started = False

    def on_event(self, event):
        log_prefix = "%s on_event:" % (self.log_prefix,)

        try:
            self.logger.info("%s recv event %s" % (log_prefix, event))

            if event.event_type in [
                MasterChannelEventType.MASTER_CHANNEL_CONNECTED,
                MasterChannelEventType.MASTER_CHANNEL_DISCONNECTED,
                MasterChannelEventType.FROM_MASTER_STOMP_MESSAGE,
            ]:
                return self.handler_master_channel.on_event(event)
            elif event.event_type in [
                WorkerAppEventType.DUMP_STATE_STORE_TIMEOUT,
                WorkerAppEventType.MASTER_REGISTER_TIMEOUT,
                WorkerAppEventType.JOB_FINISH,
                WorkerAppEventType.INDICATE_JOB_RESULT_TIMEOUT,
            ]:
                return self.handler_app.on_event(event)
        except Exception as e:
            self.logger.error("%s met Exception %s" % (log_prefix, str(e)))
            self.logger.error("%s stack %s" % (log_prefix, traceback.format_exc()))

    @property
    def active_jobs(self):
        return self.get_state(WorkerStateKey.ACTIVE_JOBS)

    @property
    def active_job_result_inds(self):
        return self.get_state(WorkerStateKey.ACTIVE_JOB_RESULT_INDS)

    def get_state(self, state_key):
        if isinstance(state_key, Enum):
            state_key = state_key.name
        return self.state_store.get(state_key)

    def set_state(self, state_key, state_value):
        if isinstance(state_key, Enum):
            state_key = state_key.name
        self.state_store[state_key] = state_value

    def dump_state_store(self):
        event_handler = BaseInRequestHandler(
            log_prefix=f"{self.log_prefix} dump_state_store:",
            logger_name=self.logger,
            elk_record_type=WorkerAppEventType.DUMP_STATE_STORE,
            req_src=ReqSrc.LocalEvent
        )

        dumped_response = {
            "active_jobs_len": len(self.active_jobs),
            "state_store": self.state_store,
        }
        event_handler.log_response_with_elk(dumped_response)

    def send_msg_to_master(self, stomp_msg, msg_id, description=None):
        event = BaseEvent(MasterChannelEventType.TO_MASTER_STOMP_MESSAGE)
        event.content["msg_id"] = msg_id
        if description:
            event.content["description"] = description
        event.content_extra["stomp_msg"] = stomp_msg
        MasterChannelSignal.TO_MASTER_CHANNEL_EVENT.send(self, event=event)

    def _init_module_config(self):
        config_inst = self.config_inst

        if "worker_app" not in config_inst:
            config_inst.worker_app = _config.Params()

        worker_app_config = config_inst.worker_app

        worker_app_config.job_dir = os.path.join(config_inst.project_dir, "jobs")

        IOUtil.maybe_make_dir(worker_app_config.job_dir)

        if not hasattr(worker_app_config, "dump_state_store_timeout"):
            worker_app_config.dump_state_store_timeout = 3600

        if hasattr(worker_app_config, "job_dir_base_url"):
            pod_name = os.environ.get('MY_POD_NAME', None)
            if pod_name:
                worker_app_config.job_dir_url = "%s/zhiyoufy-worker/%s/jobs/" % (
                    worker_app_config.job_dir_base_url, pod_name
                )
            else:
                worker_app_config.job_dir_url = f"{worker_app_config.job_dir_base_url}/jobs/"
        else:
            worker_app_config.job_dir_url = "job_dir_base_url_not_set/"

        if not hasattr(worker_app_config, "job_output_keep_days"):
            worker_app_config.job_output_keep_days = 30

        if not hasattr(worker_app_config, "remove_filter_list"):
            worker_app_config.remove_filter_list = ["/output/data_cache"]

        if not hasattr(worker_app_config, "remove_old_indeed"):
            worker_app_config.remove_old_indeed = True

    def _on_timeout_dump_state(self):
        log_prefix = f"{self.log_prefix} _on_timeout_dump_state:"

        self.logger.info(f"{log_prefix} Enter")

        self.dump_state_timer = None

        if self.app_started:
            self.dump_state_timer = threading.Timer(self.worker_app_config.dump_state_store_timeout,
                                                    self._on_timeout_dump_state)
            self.dump_state_timer.start()

            self.send_simple_event_to_handler(WorkerAppEventType.DUMP_STATE_STORE_TIMEOUT)

            # 删除本worker产生的老job输出
            self.logger.info(f"{log_prefix} to remove local worker old output")
            IOUtil.rm_old(self.worker_app_config.job_dir,
                          days=self.worker_app_config.job_output_keep_days,
                          remove_indeed=self.worker_app_config.remove_old_indeed)

            # 更新本pod对应alive标识flag
            # 删除已经结束的pod产生的输出
            if self.global_context.pod_name:
                self.logger.info(f"{log_prefix} to remove global pod old output")

                keep_alive_file_path = os.path.join(self.config_inst.pod_output_dir,
                                                    self.global_context.pod_name, "alive.flag")
                IOUtil.rm_file(keep_alive_file_path)
                with open(keep_alive_file_path, "w") as fh:
                    fh.write("%s" % datetime.utcnow())

                IOUtil.rm_old(self.config_inst.pod_output_dir,
                              days=self.worker_app_config.job_output_keep_days,
                              filter_list=self.worker_app_config.remove_filter_list,
                              remove_indeed=self.worker_app_config.remove_old_indeed)

