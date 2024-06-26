class GeneralConnectSession:
    def __init__(self, session_id):
        self.session_id = session_id
        self.disconnect_requested = False
