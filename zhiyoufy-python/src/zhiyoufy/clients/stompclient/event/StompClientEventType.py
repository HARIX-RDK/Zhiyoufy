from enum import Enum, auto


class StompClientEventType(Enum):
    WAIT_CONNECTED_RSP_TIMEOUT = auto()
