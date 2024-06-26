import enum

from marshmallow import ValidationError
from zhiyoufy.common import zhiyoufy_context
from zhiyoufy.common.handle.request_handle_util import RequestHandleUtil
from zhiyoufy.common.models import BaseResultErrorType
from zhiyoufy.common.utils import StrUtil

from .trace_record import TraceRecord


class BaseInRequestHandler:
    def __init__(self, log_prefix, logger_name, elk_record_type, elk_on=True, schema_cls=None,
                 req_src=None, guid=None, root_extra=None, user=None, user_name=None):
        self.handle_context = RequestHandleUtil.build_handle_context(
            log_prefix, logger_name, elk_record_type, elk_on=elk_on, guid=guid, root_extra=root_extra)
        self.schema_cls = schema_cls
        self.req_src = req_src

        self._user = user
        self._user_name = user_name
        self._json_data = None
        self._text_data = None
        self.loaded_req = None

        global_context = zhiyoufy_context.get_global_context()
        self.global_context = global_context

        if (self.user_name and self.user_name in global_context.need_trace_records) or \
                "*" in global_context.need_trace_records:
            self.handle_context["need_trace_records"] = True
        else:
            self.handle_context["need_trace_records"] = False


    @property
    def config_inst(self):
        _config_inst = zhiyoufy_context.get_config_inst()
        return _config_inst

    @property
    def db_mgr(self):
        return self.global_context.db_mgr

    @property
    def jwt_mgr(self):
        return self.global_context.jwt_mgr

    @property
    def log_prefix(self):
        return self.handle_context["log_prefix"]

    @property
    def logger(self):
        return self.handle_context["logger"]

    @property
    def guid(self):
        return self.handle_context["guid"]

    @guid.setter
    def guid(self, new_guid):
        self.handle_context["guid"] = new_guid
        self.handle_context["elk_record"]["guid"] = new_guid

    @property
    def elk_record_type(self):
        return self.elk_record["type"]

    @elk_record_type.setter
    def elk_record_type(self, new_elk_record_type):
        if isinstance(new_elk_record_type, enum.Enum):
            self.elk_record["type"] = new_elk_record_type.name
        else:
            self.elk_record["type"] = new_elk_record_type

    @property
    def elk_record(self):
        return self.handle_context["elk_record"]

    @property
    def elk_module_root(self):
        return self.handle_context["elk_module_root"]

    @property
    def trace_records(self):
        return self.handle_context["trace_records"]

    @property
    def need_trace_records(self):
        return self.handle_context["need_trace_records"]

    @property
    def user(self):
        return self._user

    @property
    def user_name(self):
        if self._user_name is None:
            self._user_name = self.user.name if self.user else None
        return self._user_name

    @property
    def json_data(self):
        return self._json_data

    @property
    def text_data(self):
        return self._text_data

    @property
    def page(self):
        return None

    @property
    def page_size(self):
        return None

    def process_on_recv(self):
        self.elk_module_root["req"]["req_src"] = self.req_src

        if self.user_name:
            self.elk_module_root["req"]["caller"] = self.user_name

    def load_req(self, dump_log=True, log_detail=None):
        logger = self.handle_context["logger"]
        log_prefix = self.handle_context["log_prefix"]
        trace_records = self.handle_context.get("trace_records", None)
        elk_module_root = self.handle_context.get("elk_module_root", None)

        if log_detail is None:
            log_detail = "req by %s" % self.user_name

        schema = self.schema_cls()
        try:
            loaded_req = schema.load(self.json_data)

            dumped_loaded_req = schema.dump(loaded_req)

            if dump_log:
                trace_msg = "%s %s loaded_req %s" % (
                    log_prefix, log_detail, StrUtil.pprint(dumped_loaded_req))
                logger.info(trace_msg)
                if self.need_trace_records and trace_records is not None:
                    tr = TraceRecord(trace=trace_msg, append_time=True)
                    trace_records.append(tr)

            if elk_module_root is not None:
                if "req" in elk_module_root:
                    elk_module_root["req"]["body"] = dumped_loaded_req
                else:
                    elk_module_root["req"] = {
                        "body": dumped_loaded_req
                    }

            self.loaded_req = loaded_req

            return loaded_req
        except ValidationError as err:
            if self.text_data is not None:
                elk_module_root["req"]["text_body"] = self.text_data
            return self.build_base_response(
                BaseResultErrorType.RES_ERR_BAD_FORMAT, 400, err_detail=str(err))

    def build_base_response(self, err_enum, status_code=None, err_detail=None, exception=None):
        return RequestHandleUtil.build_base_response(err_enum, self.handle_context,
                                                     err_detail=err_detail, exception=exception)

    def log_response_with_elk(self, dumped_response, root_extra=None):
        RequestHandleUtil.log_response_with_elk(
            dumped_response, handle_context=self.handle_context, root_extra=root_extra)

