from flask import make_response, jsonify

from .base_in_request_handler import BaseInRequestHandler
from .req_src import ReqSrc


class FlaskRequestHandler(BaseInRequestHandler):
    def __init__(self, g=None, request=None, **kwargs):
        self.g = g

        super().__init__(req_src=ReqSrc.FlaskView, **kwargs)

        self.request = request

    @property
    def user(self):
        if self._user is None:
            self._user = self.g.user
        return self._user

    @property
    def json_data(self):
        if self._json_data is None:
            self._json_data = self.request.get_json()
        return self._json_data

    @property
    def text_data(self):
        if self._text_data is None:
            self._text_data = self.request.get_data(as_text=True)
        return self._text_data

    def build_base_response(self, err_enum, status_code=None, err_detail=None, exception=None):
        dumped_response = super().build_base_response(
            err_enum, status_code=status_code, err_detail=err_detail, exception=exception)
        return make_response(jsonify(dumped_response)), status_code

    def build_response(self, dumped_response, status_code=None, root_extra=None):
        self.log_response_with_elk(dumped_response, root_extra)
        return make_response(jsonify(dumped_response)), status_code

