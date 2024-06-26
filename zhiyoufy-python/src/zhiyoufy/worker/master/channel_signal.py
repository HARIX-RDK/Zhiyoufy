from blinker import signal


class MasterChannelSignal:
    FROM_MASTER_CHANNEL_EVENT = signal('FROM_MASTER_CHANNEL_EVENT')
    TO_MASTER_CHANNEL_EVENT = signal('TO_MASTER_CHANNEL_EVENT')
