from enum import Enum, auto


class WorkerStateKey(Enum):
    REGISTRATION = auto()
    ACTIVE_JOBS = auto()
    ACTIVE_JOB_RESULT_INDS = auto()
