from enum import IntEnum, auto


class BaseElkRecordType(IntEnum):
    AUTH_LOGIN = auto()
    AUTH_LOGOUT = auto()
