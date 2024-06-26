import threading


class StompRpcStream:
    def __init__(self, id):
        self.id = id
        self.condition = threading.Condition()
        self.message_list = []
