from blinker import signal

from .base_handler_runnable import BaseHandlerRunnable


class BaseSignalHandlerRunnable(BaseHandlerRunnable):
    def __init__(self):
        super().__init__()

        # 同步处理可以直接得到结果
        self.sync_signals = []
        self.sync_signal_names = []

        # 异步处理主要适合不需要同步获得结果，这样通过handler队列处理就是单线程
        # 就不需要考虑共享资源使用了
        # 如果确实要block获得结果可以传一个condition过来做同步
        self.async_signals = []
        self.async_signal_names = []

    def start_handler_and_connect_signals(self):
        self.start_handler()

        self.connect_signals()

    def connect_signals(self):
        self.connect_sync_signals()
        self.connect_async_signals()

    def connect_sync_signals(self):
        for sync_signal in self.sync_signals:
            sync_signal.connect(self.on_sync_signal)

        for sync_signal_name in self.sync_signal_names:
            signal(sync_signal_name).connect(self.on_sync_signal)

    def connect_async_signals(self):
        for async_signal in self.async_signals:
            async_signal.connect(self.on_async_signal)

        for async_signal_name in self.async_signal_names:
            signal(async_signal_name).connect(self.on_async_signal)

    def disconnect_signals_and_stop_handler(self, timeout=10):
        self.disconnect_signals()

        self.stop_handler(timeout=timeout)

    def disconnect_signals(self):
        self.disconnect_sync_signals()
        self.disconnect_async_signals()

    def disconnect_sync_signals(self):
        for sync_signal in self.sync_signals:
            sync_signal.disconnect(self.on_sync_signal)

        for sync_signal_name in self.sync_signal_names:
            signal(sync_signal_name).disconnect(self.on_sync_signal)

    def disconnect_async_signals(self):
        for async_signal in self.async_signals:
            async_signal.disconnect(self.on_async_signal)

        for async_signal_name in self.async_signal_names:
            signal(async_signal_name).disconnect(self.on_async_signal)

    def on_sync_signal(self, sender, event):
        if not self.handler_started:
            self.logger.error("%s sync signal got while not started yet, event %s" % (self.log_prefix, event))
            return
        return self.on_event(event)

    def on_async_signal(self, sender, event):
        if not self.handler_started:
            self.logger.error("%s async signal got while not started yet, event %s" % (self.log_prefix, event))
            return
        self.send_event_to_handler(event)
