from zhiyoufy.worker.master import MasterChannelEventType

from zhiyoufy.worker.app.worker_app_bridge import WorkerAppBridge


class WorkerAppHandlerMasterChannel(WorkerAppBridge):
    def __init__(self, **kwargs):
        super().__init__(**kwargs)

        self.log_prefix = f"{type(self).__name__}:"

    def on_event(self, event):
        log_prefix = f"{self.log_prefix} on_event:"

        self.logger.info("%s Enter" % log_prefix)

        if event.event_type == MasterChannelEventType.MASTER_CHANNEL_CONNECTED:
            return self._on_master_channel_connected(event)
        elif event.event_type == MasterChannelEventType.MASTER_CHANNEL_DISCONNECTED:
            return self._on_master_channel_disconnected(event)
        elif event.event_type == MasterChannelEventType.FROM_MASTER_STOMP_MESSAGE:
            return self._on_master_stomp_message(event)

        self.logger.info("%s Leave" % log_prefix)

    def _on_master_channel_connected(self, event):
        self.register_manager.on_master_channel_connected()

    def _on_master_channel_disconnected(self, event):
        self.register_manager.on_master_channel_disconnected()

    def _on_master_stomp_message(self, event):
        stomp_msg = event.content["stomp_msg"]

        if stomp_msg["cmd"] == "MESSAGE":
            destination = stomp_msg["headers"].get("destination", None)

            if destination == "/app/worker-register-rsp":
                self.register_manager.on_register_rsp(stomp_msg)
            elif destination == "/app/start-job-child-run-req":
                self.job_manager.on_start_job_child_run_req(stomp_msg)
            elif destination == "/app/stop-job-child-run-req":
                self.job_manager.on_stop_job_child_run_req(stomp_msg)
            elif destination == "/app/job-child-run-result-rsp":
                self.job_manager.on_job_child_run_result_rsp(stomp_msg)

    def _on_stop_worker_req(self, in_master_msg):
        # log_prefix = "%s _on_stop_worker_req:" % (self.log_prefix,)
        #
        # out_master_msg = self._build_base_rsp(
        #     in_master_msg,
        #     master_pb2.JobMessageType.STOP_WORKER_RSP,
        #     BaseResultErrorType.RES_OK)
        #
        # self.logger.info("%s send stop_worker_rsp with guid %s" % (
        #     log_prefix, out_master_msg.common_header.guid))
        #
        # self._send_msg_to_master(out_master_msg)
        #
        # active_worker = ActiveWorker()
        # active_worker.update(CommonUtil.load_grpc_msg(in_master_msg.stop_worker_req.active_worker))
        #
        # worker_runner = self.worker_runners.get(active_worker.worker_key)
        # if worker_runner:
        #     worker_runner.stop()
        pass
