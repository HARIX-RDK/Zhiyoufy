from marshmallow import Schema, fields, post_load, post_dump

from zhiyoufy.common.utils import TimeUtil


class TraceRecord:
    def __init__(self, trace, children=None, append_time=False):
        if not append_time:
            self.trace = trace
        else:
            cur_time_display = TimeUtil.get_current_time_isoformat()
            self.trace = "%s  %s" % (cur_time_display, trace)
        self.children = children


class TraceRecordSchema(Schema):
    trace = fields.Str(required=True)
    children = fields.List(fields.Nested(lambda: TraceRecordSchema()))

    @post_dump
    def remove_skip_values(self, data, many=False):
        if not data["children"]:
            del data["children"]
        return data

    @post_load
    def make_trace_record(self, data, **kwargs):
        return TraceRecord(**data)
