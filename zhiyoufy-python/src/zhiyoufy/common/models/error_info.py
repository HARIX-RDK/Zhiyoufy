from marshmallow import fields
from enum import Enum

from .base_schema import BaseSchema


class ErrorInfo:
    def __init__(self, code=None, message=None, err_enum=None, detail=None):
        if isinstance(err_enum, Enum):
            self.code = int(err_enum)
            self.message = err_enum.name
        else:
            self.code = code
            self.message = message

        self.detail = detail


class ErrorInfoSchema(BaseSchema):
    code = fields.Number()
    message = fields.Str()
    detail = fields.Str()
