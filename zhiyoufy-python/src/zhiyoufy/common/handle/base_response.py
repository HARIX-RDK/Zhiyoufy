from marshmallow import fields, validate, post_dump, post_load
from enum import Enum

from zhiyoufy.common.handle.trace_record import TraceRecordSchema
from zhiyoufy.common.models.base_schema import BaseSchema


class BaseResponse:
    def __init__(self, err_enum, err_detail=None, trace_records=None):
        if isinstance(err_enum, Enum):
            self.err_code = int(err_enum)
            self.err_msg = err_enum.name
        else:
            self.err_code = err_enum[0]
            self.err_msg = err_enum[1]
        self.err_detail = err_detail
        self.trace_records = trace_records

    def dump(self):
        schema = BaseResponseSchema()
        return schema.dump(self)


class BaseResponseSchema(BaseSchema):
    err_code = fields.Integer()
    err_msg = fields.Str(required=True, validate=validate.Length(min=2))
    err_detail = fields.Str()
    trace_records = fields.List(fields.Nested(TraceRecordSchema))

    @post_dump
    def remove_empty_values(self, data, many=False):
        if not data["err_detail"]:
            del data["err_detail"]
        if not data["trace_records"]:
            del data["trace_records"]
        return data

    @post_load
    def make_obj(self, data, **kwargs):
        return BaseResponse(**data)
