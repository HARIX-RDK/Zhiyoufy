import threading
import logging
import bisect

from ..utils import TimeUtil


class TimerEventQueue:
    def __init__(self):
        self.tag = type(self).__name__
        self.log_prefix = "%s:" % self.tag
        self.logger = logging.getLogger(self.tag)

        self.default_timeout = 5

        self.timer_event_queue_lock = threading.Lock()

        self.timer_event_queue = []
        self.timer_event_fire_time_queue = []
        self.timer_event_map = {}

        self.process_timer = None
        self.process_timer_target_time = None

    def process_timer_event_queue(self):
        with self.timer_event_queue_lock:
            self.process_timer.cancel()
            self.process_timer = None

            while self.timer_event_fire_time_queue:
                current_monotonic = TimeUtil.get_current_time_monotonic()
                first_fire_time = self.timer_event_fire_time_queue[0]

                if current_monotonic > first_fire_time:
                    fired_event = self.timer_event_queue[0]
                    del self.timer_event_fire_time_queue[0]
                    del self.timer_event_queue[0]

                    self.logger.info("%s process_timer_event_queue guid %s fired" % (
                        self.log_prefix, fired_event.guid))
                    del self.timer_event_map[fired_event.guid]
                    fired_event.on_fired()
                    continue

                self.process_timer_target_time = first_fire_time
                timeout = self.process_timer_target_time - current_monotonic
                self.process_timer = threading.Timer(timeout, self.process_timer_event_queue)
                self.process_timer.start()
                return

    def pop_timer_event(self, event_guid, log_miss=True):
        with self.timer_event_queue_lock:
            timer_event = self.timer_event_map.get(event_guid)
            if not timer_event:
                if log_miss:
                    self.logger.info("%s pop_timer_event, no event for event_guid %s" % (
                        self.log_prefix, event_guid))
                return None
            self.logger.info("%s pop_timer_event guid %s" % (self.log_prefix, event_guid))
            index = self.timer_event_queue.index(timer_event)
            del self.timer_event_fire_time_queue[index]
            del self.timer_event_queue[index]
            del self.timer_event_map[event_guid]
            return timer_event

    def push_timer_event(self, timer_event, timeout=None):
        if timeout is None:
            timeout = self.default_timeout

        with self.timer_event_queue_lock:
            self.logger.info("%s push_timer_event guid %s, timeout %s" % (
                self.log_prefix, timer_event.guid, timeout))
            current_monotonic = TimeUtil.get_current_time_monotonic()
            fire_time = current_monotonic + timeout
            insert_idx = bisect.bisect(self.timer_event_fire_time_queue, fire_time)
            self.timer_event_fire_time_queue.insert(insert_idx, fire_time)
            self.timer_event_queue.insert(insert_idx, timer_event)
            self.timer_event_map[timer_event.guid] = timer_event

            if self.process_timer is not None and \
                    self.process_timer_target_time > fire_time:
                self.process_timer.cancel()
                self.process_timer = None

            if self.process_timer is None:
                self.process_timer_target_time = fire_time
                timeout = fire_time - current_monotonic
                self.process_timer = threading.Timer(timeout, self.process_timer_event_queue)
                self.process_timer.start()
