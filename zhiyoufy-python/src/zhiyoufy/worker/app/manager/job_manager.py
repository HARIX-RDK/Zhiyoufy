import json
import os

from zhiyoufy.common.handle import BaseOutRequestHandler, ReqSrc
from zhiyoufy.common.models import BaseResultErrorType, BaseEvent, SendEventTimerEvent
from zhiyoufy.common.utils import RandomUtil
from zhiyoufy.stomper import Frame
from zhiyoufy.worker.app.bo import ActiveJob, JobRunner
from zhiyoufy.worker.app.worker_app_bridge import WorkerAppBridge
from zhiyoufy.worker.app.worker_app_event import WorkerAppEventType


class JobManager(WorkerAppBridge):
    def __init__(self, **kwargs):
        super().__init__(**kwargs)

        self.tag = type(self).__name__
        self.log_prefix = f"{self.tag}:"

    def on_start_job_child_run_req(self, stomp_msg):
        log_prefix = "%s on_start_job_child_run_req:" % (self.log_prefix,)

        # ignore_start_job_child_run_req_flag_path = os.path.join(
        #     self.config_inst.root_dir, "private_ignore_start_job_child_run_req.flag")
        # if os.path.exists(ignore_start_job_child_run_req_flag_path):
        #     return

        req_body = json.loads(stomp_msg["body"])
        worker_app_config = self.config_inst.worker_app

        if len(self.active_jobs) >= worker_app_config.max_active_job_num:
            stomp_msg_frame = self._build_start_job_child_run_rsp(
                req_body, BaseResultErrorType.RES_ERR_RESOURCE_NOT_AVAILABLE,
                err_detail="active_jobs len %s reach max_active_job_num %s" % (
                    len(self.active_jobs), worker_app_config.max_active_job_num
                )
            )

            destination = stomp_msg_frame.headers["destination"]
            msg_id = stomp_msg_frame.body["messageId"]

            stomp_msg_binary = stomp_msg_frame.pack_json_binary()

            self.logger.error("%s send start_job_child_run_rsp with msg_id %s" % (log_prefix, msg_id))

            self.send_msg_to_master(stomp_msg_binary, msg_id, description=destination)

            return

        stomp_msg_frame = self._build_start_job_child_run_rsp(req_body)

        destination = stomp_msg_frame.headers["destination"]
        msg_id = stomp_msg_frame.body["messageId"]

        stomp_msg_binary = stomp_msg_frame.pack_json_binary()

        self.logger.info("%s send start_job_child_run_rsp with msg_id %s" % (log_prefix, msg_id))

        self.send_msg_to_master(stomp_msg_binary, msg_id, description=destination)

        active_job = ActiveJob(**req_body)
        self.active_jobs[active_job.job_key] = active_job

        job_runner = JobRunner()
        job_runner.active_job = active_job
        self.job_runners[job_runner.job_key] = job_runner

        job_runner.run_output_dir = os.path.join(worker_app_config.job_dir, active_job.job_dir_key)
        job_runner.test_suite_path = req_body["jobPath"]
        job_runner.base_conf_path = req_body["baseConfPath"]
        job_runner.private_conf_path = req_body["privateConfPath"]
        if req_body["extraArgs"]:
            job_runner.extra_args = req_body["extraArgs"]
        job_runner.timeout_seconds = req_body["timeoutSeconds"]
        job_runner.config_composite = req_body["configComposite"]

        job_runner.finish_callback = self._callback_job_finish
        job_runner.finish_callback_args = (job_runner,)

        job_runner.start()

    def on_stop_job_child_run_req(self, stomp_msg):
        log_prefix = "%s on_stop_job_child_run_req:" % (self.log_prefix,)

        req_body = json.loads(stomp_msg["body"])

        stomp_msg_frame = self._build_stop_job_child_run_rsp(req_body)

        destination = stomp_msg_frame.headers["destination"]
        msg_id = stomp_msg_frame.body["messageId"]

        stomp_msg_binary = stomp_msg_frame.pack_json_binary()

        self.logger.info("%s send stop_job_child_run_rsp with msg_id %s" % (log_prefix, msg_id))

        self.send_msg_to_master(stomp_msg_binary, msg_id, description=destination)

        active_job = ActiveJob(**req_body)

        job_runner = self.job_runners.get(active_job.job_key)

        if job_runner:
            job_runner.stop()

    def on_job_finish(self, event):
        log_prefix = "%s on_job_finish:" % (self.log_prefix,)

        job_runner = event.content_extra["job_runner"]
        active_job = job_runner.active_job

        del self.active_jobs[active_job.job_key]

        outbound_req_handler = self._indicate_job_result_to_master(job_runner)

        event = BaseEvent(WorkerAppEventType.INDICATE_JOB_RESULT_TIMEOUT)
        event.content["job_key"] = job_runner.active_job.job_key
        event.content["sent_cnt"] = 1
        event.content_extra["job_runner"] = job_runner
        event.content_extra["outbound_req_handler"] = outbound_req_handler

        timer_event = SendEventTimerEvent(handler_runnable=self, event=event, guid=outbound_req_handler.guid)
        self.timer_event_queue.push_timer_event(timer_event)

    def on_job_child_run_result_rsp(self, stomp_msg):
        log_prefix = "%s on_job_child_run_result_rsp:" % (self.log_prefix,)

        rsp_body = json.loads(stomp_msg["body"])

        req_guid = rsp_body["messageId"]

        timer_event = self.timer_event_queue.pop_timer_event(req_guid)

        if not timer_event:
            self.logger.error("%s no pending job result ind for guid %s" % (log_prefix, req_guid))

            return

        outbound_req_handler = timer_event.event.content_extra["outbound_req_handler"]

        outbound_req_handler.log_response_with_elk(rsp_body)

    @staticmethod
    def _build_start_job_child_run_rsp(req_body, master_err=None, err_detail=""):
        destination = "/app/start-job-child-run-rsp"
        msg_id = RandomUtil.gen_guid()

        body = {
            "runGuid": req_body["runGuid"],
            "index": req_body["index"],
            "messageId": msg_id,
        }

        if master_err:
            body["error"] = {
                "code": int(master_err),
                "message": master_err.name,
            }

            if err_detail:
                body["error"]["detail"] = err_detail

        stomp_msg_frame = Frame()
        stomp_msg_frame.cmd = "SEND"
        stomp_msg_frame.headers = {
            "destination": destination,
        }
        stomp_msg_frame.body = body

        return stomp_msg_frame

    @staticmethod
    def _build_stop_job_child_run_rsp(req_body, master_err=None, err_detail=""):
        destination = "/app/stop-job-child-run-rsp"
        msg_id = RandomUtil.gen_guid()

        body = {
            "runGuid": req_body["runGuid"],
            "index": req_body["index"],
            "messageId": msg_id,
        }

        if master_err:
            body["error"] = {
                "code": int(master_err),
                "message": master_err.name,
            }

            if err_detail:
                body["error"]["detail"] = err_detail

        stomp_msg_frame = Frame()
        stomp_msg_frame.cmd = "SEND"
        stomp_msg_frame.headers = {
            "destination": destination,
        }
        stomp_msg_frame.body = body

        return stomp_msg_frame

    def _callback_job_finish(self, job_runner):
        log_prefix = "%s _callback_job_finish:" % (self.log_prefix,)

        self.logger.info("%s Enter active_job %s" % (log_prefix, job_runner.active_job))

        del self.job_runners[job_runner.job_key]

        event = BaseEvent(WorkerAppEventType.JOB_FINISH)
        event.content["job_key"] = job_runner.active_job.job_key
        event.content_extra["job_runner"] = job_runner
        self.send_event_to_handler(event)

    def _indicate_job_result_to_master(self, job_runner):
        log_prefix = "%s _indicate_job_result_to_master:" % self.log_prefix

        worker_app_config = self.config_inst.worker_app
        active_job = job_runner.active_job

        destination = "/app/job-child-run-result-ind"
        msg_id = job_runner.result_guid

        body = {
            "messageId": msg_id,
            "runGuid": active_job["runGuid"],
            "index": active_job["index"],

            "endOk": job_runner.end_ok,
            "resultOk": job_runner.result_ok,
            "passed": job_runner.passed,
            "jobOutputUrl": f"{worker_app_config.job_dir_url}{active_job.job_dir_key}/",
        }

        outbound_req_handler = BaseOutRequestHandler(
            log_prefix, self.logger,
            elk_record_type=WorkerAppEventType.OUT_JOB_RESULT_IND,
            req_src=ReqSrc.WebSocket,
            guid=msg_id,
            req_msg=body,
        )

        stomp_msg_frame = Frame()
        stomp_msg_frame.cmd = "SEND"
        stomp_msg_frame.headers = {
            "destination": destination,
        }
        stomp_msg_frame.body = body

        stomp_msg_binary = stomp_msg_frame.pack_json_binary()

        self.logger.info("%s send job_result_ind with guid %s" % (log_prefix, msg_id))

        self.send_msg_to_master(stomp_msg_binary, msg_id, description=destination)

        return outbound_req_handler
