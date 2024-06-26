import logging
import threading
from datetime import datetime, timedelta
import time
import collections

from zhiyoufy.common import zhiyoufy_context
from zhiyoufy.common.models import BaseEvent


class BaseHandlerRunnable:
    def __init__(self):
        self.tag = type(self).__name__
        self.log_prefix = self.tag
        self.logger = logging.getLogger(self.tag)

        self.config_inst = zhiyoufy_context.get_config_inst()
        self.global_context = zhiyoufy_context.get_global_context()
        self.timer_event_queue = self.global_context.timer_event_queue
        self.schedule_event_queue = self.global_context.schedule_event_queue
        self.handler_started = False

        self.stopping = False
        self.handler_thread = None
        self.handler_event_queue = collections.deque()
        self.handler_event_queue_condition = threading.Condition()
        self.handler_event_queue_empty = True

        if "alive_log_period" in self.config_inst:
            self.alive_log_period = self.config_inst.alive_log_period
        else:
            self.alive_log_period = 600

    def send_simple_event_to_handler(self, event_type):
        event = BaseEvent(event_type=event_type)
        self.send_event_to_handler(event)

    def send_event_to_handler(self, event):
        self.handler_event_queue.append(event)
        if self.handler_event_queue_empty:
            with self.handler_event_queue_condition:
                if self.handler_event_queue_empty:
                    self.handler_event_queue_empty = False
                    self.handler_event_queue_condition.notify()

    def handler_thread_run(self):
        log_prefix = "%s handler_thread_run:" % (self.log_prefix,)
        last_log_time = datetime.utcnow()

        while not self.stopping:
            # with self.handler_event_queue_condition:
            if self.handler_event_queue:
                first_event = self.handler_event_queue.popleft()
            else:
                if (datetime.utcnow() - last_log_time) > timedelta(seconds=self.alive_log_period):
                    self.logger.info("%s alive" % log_prefix)
                    last_log_time = datetime.utcnow()
                with self.handler_event_queue_condition:
                    if not self.handler_event_queue and not self.stopping:
                        self.handler_event_queue_empty = True
                        self.handler_event_queue_condition.wait(10)
                continue

            if first_event:
                self.on_event(first_event)
            else:
                self.logger.info("%s received stop event %s" % (log_prefix, first_event))
                break

        self.logger.info("%s Leave with stopping %s" % (log_prefix, self.stopping))

        self.handler_thread = None

    def on_event(self, event):
        raise NotImplemented

    def start_handler(self):
        log_prefix = "%s start_handler:" % (self.log_prefix,)

        self.logger.info("%s Enter" % log_prefix)

        if not self.handler_started:
            self.handler_thread = threading.Thread(
                target=self.handler_thread_run, daemon=True,
                name="%s_%s" % (self.tag, "handler_thread_run"))
            self.handler_thread.start()

            self.handler_started = True

        self.logger.info("%s Leave" % log_prefix)

    def stop_handler(self, timeout=10):
        log_prefix = "%s stop_handler:" % (self.log_prefix,)

        self.logger.info("%s Enter" % log_prefix)

        if self.handler_started:
            self.stopping = True

            final_time = datetime.utcnow() + timedelta(seconds=timeout)

            with self.handler_event_queue_condition:
                self.handler_event_queue_condition.notify()

            first = True
            while datetime.utcnow() <= final_time:
                if first:
                    time.sleep(0.2)
                    if self.handler_thread is None:
                        break
                    first = False
                else:
                    time.sleep(1)
                    if self.handler_thread is None:
                        break

            self.handler_started = False

            if self.handler_thread is not None:
                raise Exception("%s timeout" % log_prefix)

        self.logger.info("%s Leave" % log_prefix)
