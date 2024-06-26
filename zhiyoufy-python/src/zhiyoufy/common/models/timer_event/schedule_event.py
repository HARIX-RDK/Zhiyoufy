from zhiyoufy.common.utils import RandomUtil


class ScheduleEvent:
    def __init__(self, crontab_config, schedule_id=None):
        self.crontab_config = crontab_config

        if schedule_id:
            self.schedule_id = schedule_id
        else:
            self.schedule_id = RandomUtil.gen_guid()

    def on_fired(self):
        raise NotImplemented
