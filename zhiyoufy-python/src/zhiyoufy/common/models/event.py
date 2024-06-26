from datetime import datetime

from zhiyoufy.common.utils import StrUtil


class BaseEvent:
    def __init__(self, event_type, content=None, content_extra=None):
        self.event_type = event_type
        self.content = content if content else {}
        self.content_extra = content_extra if content_extra else {}
        self.created_on = datetime.utcnow()

    def __str__(self):
        return "%s: event_type=%s, created_on %s, content=\n%s" % (
            type(self).__name__, self.event_type, self.created_on, StrUtil.pprint(self.content))
