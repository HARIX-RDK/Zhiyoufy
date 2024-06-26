from zhiyoufy.common.utils import RandomUtil


class TimerEvent:
    def __init__(self, guid=None):
        if guid:
            self.guid = guid
        else:
            self.guid = RandomUtil.gen_guid()

    def on_fired(self):
        raise NotImplemented
