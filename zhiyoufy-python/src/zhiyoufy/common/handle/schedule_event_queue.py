import threading
import logging
import bisect
from datetime import datetime

from croniter import croniter


class ScheduleEventQueue:
    def __init__(self):
        self.tag = type(self).__name__
        self.log_prefix = "%s:" % self.tag
        self.logger = logging.getLogger(self.tag)

        self.schedule_event_queue_lock = threading.Lock()

        self.schedule_event_queue = []
        self.schedule_event_fire_time_queue = []
        self.schedule_event_map = {}

        self.process_timer = None
        self.process_timer_target_time = None

    def process_schedule_event_queue(self):
        with self.schedule_event_queue_lock:
            self.process_timer.cancel()
            self.process_timer = None

            while self.schedule_event_fire_time_queue:
                current_time = datetime.utcnow()
                first_fire_time = self.schedule_event_fire_time_queue[0]

                if current_time >= first_fire_time:
                    fired_event = self.schedule_event_queue[0]
                    del self.schedule_event_fire_time_queue[0]
                    del self.schedule_event_queue[0]

                    self.logger.info("%s process_schedule_event_queue schedule_id %s fired" % (
                        self.log_prefix, fired_event.schedule_id))
                    del self.schedule_event_map[fired_event.schedule_id]
                    self.push_schedule_event_run(fired_event, first_fire_time,
                                                 manage_process_timer=False)
                    fired_event.on_fired()
                    continue

                self.process_timer_target_time = first_fire_time
                timeout = (self.process_timer_target_time - current_time).total_seconds()
                self.process_timer = threading.Timer(timeout, self.process_schedule_event_queue)
                self.process_timer.start()
                return

    def pop_schedule_event(self, event_schedule_id, log_miss=True):
        with self.schedule_event_queue_lock:
            schedule_event = self.schedule_event_map.get(event_schedule_id)
            if not schedule_event:
                if log_miss:
                    self.logger.info("%s pop_schedule_event, no event for event_schedule_id %s" % (
                        self.log_prefix, event_schedule_id))
                return None
            self.logger.info("%s pop_schedule_event schedule_id %s" % (self.log_prefix, event_schedule_id))
            index = self.schedule_event_queue.index(schedule_event)
            del self.schedule_event_fire_time_queue[index]
            del self.schedule_event_queue[index]
            del self.schedule_event_map[event_schedule_id]
            return schedule_event

    def push_schedule_event(self, schedule_event):
        with self.schedule_event_queue_lock:
            self.push_schedule_event_run(schedule_event)

    def push_schedule_event_run(self, schedule_event, cur_fire_time=None, manage_process_timer=True):
        cur_time = datetime.utcnow()
        iter = croniter(schedule_event.crontab_config, cur_time)
        fire_time = iter.get_next(datetime)

        if cur_fire_time is not None and fire_time == cur_fire_time:
            fire_time = iter.get_next(datetime)

        self.logger.info("%s push_schedule_event schedule_id %s, crontab_config %s, next fire_time %s" % (
            self.log_prefix, schedule_event.schedule_id, schedule_event.crontab_config, fire_time))

        insert_idx = bisect.bisect(self.schedule_event_fire_time_queue, fire_time)
        self.schedule_event_fire_time_queue.insert(insert_idx, fire_time)
        self.schedule_event_queue.insert(insert_idx, schedule_event)
        self.schedule_event_map[schedule_event.schedule_id] = schedule_event

        if manage_process_timer:
            if self.process_timer is not None and \
                    self.process_timer_target_time > fire_time:
                self.process_timer.cancel()
                self.process_timer = None

            if self.process_timer is None:
                self.process_timer_target_time = fire_time
                timeout = (fire_time - cur_time).total_seconds()
                self.process_timer = threading.Timer(timeout, self.process_schedule_event_queue)
                self.process_timer.start()
