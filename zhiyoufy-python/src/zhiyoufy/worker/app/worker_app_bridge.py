class WorkerAppBridge:
    def __init__(self, parent_app):
        self.parent_app = parent_app

    @property
    def logger(self):
        return self.parent_app.logger

    @property
    def config_inst(self):
        return self.parent_app.config_inst

    @property
    def global_context(self):
        return self.parent_app.global_context

    @property
    def active_jobs(self):
        return self.parent_app.active_jobs

    @property
    def active_job_result_inds(self):
        return self.parent_app.active_job_result_inds

    @property
    def job_runners(self):
        return self.parent_app.job_runners

    @property
    def job_manager(self):
        return self.parent_app.job_manager

    @property
    def register_manager(self):
        return self.parent_app.register_manager

    @property
    def state_store(self):
        return self.parent_app.state_store

    @property
    def timer_event_queue(self):
        return self.parent_app.timer_event_queue

    def get_state(self, state_key):
        return self.parent_app.get_state(state_key)

    def set_state(self, state_key, state_value):
        self.parent_app.set_state(state_key, state_value)

    def dump_state_store(self):
        self.parent_app.dump_state_store()

    def send_msg_to_master(self, stomp_msg, msg_id, description=None):
        self.parent_app.send_msg_to_master(stomp_msg, msg_id, description=description)

    def send_event_to_handler(self, event):
        self.parent_app.send_event_to_handler(event)

    def send_simple_event_to_handler(self, event_type):
        self.parent_app.send_simple_event_to_handler(event_type)
