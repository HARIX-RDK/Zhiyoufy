class StompControllerEndpoint:
    def __init__(self, app_destination, callback):
        self.app_destination = app_destination
        self.callback = callback
