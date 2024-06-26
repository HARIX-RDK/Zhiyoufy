import threading
import logging
import bisect

from zhiyoufy.common.utils import TimeUtil
from zhiyoufy.common.models import BaseResultErrorType


class ResponseWaiter:
    def __init__(self):
        self.tag = type(self).__name__
        self.log_prefix = "%s:" % self.tag
        self.logger = logging.getLogger(self.tag)

        self.default_timeout = 5

        self.req_wait_rsp_queue_lock = threading.Lock()
        self.req_wait_rsp_queue = []
        self.req_wait_rsp_timeout_queue = []
        self.req_wait_rsp_map = {}
        self.req_wait_rsp_timer = None
        self.req_wait_rsp_timer_target_ms = None

    def on_timer_check_req_wait_rsp_queue(self):
        with self.req_wait_rsp_queue_lock:
            self.req_wait_rsp_timer.cancel()
            self.req_wait_rsp_timer = None

            current_time_ms = TimeUtil.get_current_time_ms()
            while self.req_wait_rsp_timeout_queue:
                timeout_req_handler = None
                first_timeout = self.req_wait_rsp_timeout_queue[0]

                if current_time_ms > first_timeout or (current_time_ms + 120000) < first_timeout:
                    timeout_req_handler = self.req_wait_rsp_queue[0]
                    del self.req_wait_rsp_timeout_queue[0]
                    del self.req_wait_rsp_queue[0]

                if not timeout_req_handler:
                    last_timeout = self.req_wait_rsp_timeout_queue[-1]
                    if (current_time_ms + 120000) < last_timeout:
                        timeout_req_handler = self.req_wait_rsp_queue[-1]
                        del self.req_wait_rsp_timeout_queue[-1]
                        del self.req_wait_rsp_queue[-1]

                if timeout_req_handler:
                    self.logger.info("%s on_timer_check_req_wait_rsp_queue some req timeout" % self.log_prefix)
                    del self.req_wait_rsp_map[timeout_req_handler.guid]
                    timeout_req_handler.build_base_response(
                        BaseResultErrorType.RES_ERR_TIMEOUT, 400)
                    continue

                self.req_wait_rsp_timer_target_ms = first_timeout + 100
                timeout = (self.req_wait_rsp_timer_target_ms - current_time_ms) / 1000
                self.req_wait_rsp_timer = threading.Timer(timeout, self.on_timer_check_req_wait_rsp_queue)
                self.req_wait_rsp_timer.start()
                return

    def pop_req_for_rsp(self, req_guid):
        with self.req_wait_rsp_queue_lock:
            req_handler = self.req_wait_rsp_map.get(req_guid)
            if not req_handler:
                self.logger.info("%s pop_req_for_rsp, no handler for req_guid %s" % (self.log_prefix, req_guid))
                return None
            index = self.req_wait_rsp_queue.index(req_handler)
            del self.req_wait_rsp_timeout_queue[index]
            del self.req_wait_rsp_queue[index]
            del self.req_wait_rsp_map[req_guid]
            return req_handler

    def queue_req_for_rsp(self, req_handler, timeout=None):
        if timeout is None:
            timeout = self.default_timeout

        with self.req_wait_rsp_queue_lock:
            self.logger.info("%s queue_req_for_rsp" % self.log_prefix)
            current_time_ms = TimeUtil.get_current_time_ms()
            timeout_time_ms = current_time_ms + int(timeout * 1000)
            insert_idx = bisect.bisect(self.req_wait_rsp_timeout_queue, timeout_time_ms)
            self.req_wait_rsp_timeout_queue.insert(insert_idx, timeout_time_ms)
            self.req_wait_rsp_queue.insert(insert_idx, req_handler)
            self.req_wait_rsp_map[req_handler.guid] = req_handler

            adjusted_timeout_ms = timeout_time_ms + 100
            if self.req_wait_rsp_timer is not None and \
                    self.req_wait_rsp_timer_target_ms > (adjusted_timeout_ms + 100):
                self.req_wait_rsp_timer.cancel()
                self.req_wait_rsp_timer = None

            if self.req_wait_rsp_timer is None:
                self.req_wait_rsp_timer_target_ms = adjusted_timeout_ms
                timeout = (self.req_wait_rsp_timer_target_ms - current_time_ms) / 1000
                self.req_wait_rsp_timer = threading.Timer(timeout, self.on_timer_check_req_wait_rsp_queue)
                self.req_wait_rsp_timer.start()
