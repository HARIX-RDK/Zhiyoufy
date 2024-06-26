from enum import Enum, auto


class ActivationState(Enum):
    ACTIVE = auto()
    DEACTIVATING = auto()
    INACTIVE = auto()
