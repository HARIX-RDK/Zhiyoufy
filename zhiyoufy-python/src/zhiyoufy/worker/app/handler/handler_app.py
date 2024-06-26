from zhiyoufy.worker.app.worker_app_bridge import WorkerAppBridge
from zhiyoufy.worker.app.worker_app_event import WorkerAppEventType


class WorkerAppHandlerApp(WorkerAppBridge):
    def __init__(self, **kwargs):
        super().__init__(**kwargs)

        self.tag = type(self).__name__
        self.log_prefix = f"{self.tag}:"

    def on_event(self, event):
        log_prefix = f"{self.log_prefix} on_event:"

        self.logger.info("%s Enter" % log_prefix)

        if event.event_type == WorkerAppEventType.DUMP_STATE_STORE_TIMEOUT:
            return self._on_dump_state_store_timeout(event)
        elif event.event_type == WorkerAppEventType.MASTER_REGISTER_TIMEOUT:
            return self._on_master_register_timeout(event)
        elif event.event_type == WorkerAppEventType.JOB_FINISH:
            return self._on_job_finish(event)
        elif event.event_type == WorkerAppEventType.INDICATE_JOB_RESULT_TIMEOUT:
            return self._on_indicate_worker_result_timeout(event)

        self.logger.info("%s Leave" % log_prefix)

    def _on_dump_state_store_timeout(self, event):
        self.dump_state_store()

    def _on_master_register_timeout(self, event):
        log_prefix = "%s _on_master_register_timeout:" % (self.log_prefix,)

        self.logger.error(f"{log_prefix} register timeout")

    def _on_job_finish(self, event):
        self.job_manager.on_job_finish(event)

    def _on_indicate_worker_result_timeout(self, event):
        # outbound_req_handler = event.content_extra["outbound_req_handler"]
        # outbound_req_handler.build_base_response(BaseResultErrorType.RES_ERR_TIMEOUT, 400)
        #
        # if event.content["sent_cnt"] < 2:
        #     worker_runner = event.content_extra["worker_runner"]
        #     event.content["sent_cnt"] += 1
        #
        #     outbound_req_handler = self._indicate_worker_result_to_master(worker_runner)
        #
        #     event.content_extra["outbound_req_handler"] = outbound_req_handler
        #
        #     timer_event = SendEventTimerEvent(handler_runnable=self, event=event, guid=outbound_req_handler.guid)
        #     self.timer_event_queue.push_timer_event(timer_event)
        pass

