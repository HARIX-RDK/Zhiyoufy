from enum import Enum, auto


class WorkerAppEventType(Enum):
    DUMP_STATE_STORE_TIMEOUT = auto()

    MASTER_REGISTER_TIMEOUT = auto()
    INDICATE_JOB_RESULT_TIMEOUT = auto()

    DUMP_STATE_STORE = auto()
    JOB_FINISH = auto()

    SEND_JOB_MESSAGE_TO_MASTER = auto()

    OUT_WORKER_REGISTER_REQ = auto()
    OUT_JOB_RESULT_IND = auto()
