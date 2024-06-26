from marshmallow import fields

from .base_schema import BaseSchema
from .error_info import ErrorInfoSchema


class ErrorResponse:
    def __init__(self, error):
        self.error = error

    def dump(self):
        schema = ErrorResponseSchema()
        json_rsp = schema.dump(self)
        return json_rsp


class ErrorResponseSchema(BaseSchema):
    error = fields.Nested(ErrorInfoSchema)
