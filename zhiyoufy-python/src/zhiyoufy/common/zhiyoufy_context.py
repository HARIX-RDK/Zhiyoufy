import threading
from concurrent.futures import ThreadPoolExecutor


class GeneralObject(object):
    pass


class GlobalContext:
    def __init__(self):
        self._timer_event_queue = None
        self._schedule_event_queue = None
        self.need_trace_records = {}
        self.in_cleanup = False
        self.default_executor = ThreadPoolExecutor(thread_name_prefix='default_executor')

    @property
    def timer_event_queue(self):
        if self._timer_event_queue is None:
            from .handle.timer_event_queue import TimerEventQueue
            self._timer_event_queue = TimerEventQueue()
        return self._timer_event_queue

    @property
    def schedule_event_queue(self):
        if self._schedule_event_queue is None:
            from .handle.schedule_event_queue import ScheduleEventQueue
            self._schedule_event_queue = ScheduleEventQueue()
        return self._schedule_event_queue


config_inst = None
robot_logger = None
normal_logger = None
global_context = GlobalContext()
global_locks_create_lock = threading.Lock()
global_locks = {}


def get_global_context():
    return global_context


def get_global_lock(lock_id):
    global global_locks
    if not lock_id in global_locks:
        with global_locks_create_lock:
            if lock_id not in global_locks:
                global_locks[lock_id] = threading.Lock()
    return global_locks[lock_id]


def set_config_inst(inst):
    global config_inst
    config_inst = inst


def get_config_inst():
    global config_inst

    if config_inst is None:
        raise ValueError("config not parsed yet?")

    return config_inst


def set_robot_logger(inst):
    global robot_logger
    robot_logger = inst


def get_robot_logger():
    global robot_logger

    if robot_logger is None:
        raise ValueError("robot_logger not set yet?")

    return robot_logger


def set_normal_logger(inst):
    global normal_logger
    normal_logger = inst


def get_normal_logger():
    global normal_logger

    if normal_logger is None:
        raise ValueError("normal_logger not set yet?")

    return normal_logger
