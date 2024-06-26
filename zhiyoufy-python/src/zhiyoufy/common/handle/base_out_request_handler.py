from zhiyoufy.common import zhiyoufy_context
from .req_src import ReqSrc

from .request_handle_util import RequestHandleUtil


class BaseOutRequestHandler:
    def __init__(self, log_prefix, logger_name, elk_record_type,
                 req_src=ReqSrc.WebSocket, caller=None,
                 session=None, req_msg=None, guid=None, root_guid=None, elk_on=True):

        self.handle_context = RequestHandleUtil.build_handle_context(
            log_prefix, logger_name, elk_record_type, elk_on=elk_on, guid=guid)

        self.req_src = req_src
        self.caller = caller

        self.session = session
        self.req_msg = req_msg
        self.root_guid = root_guid
        self.guid = guid

        self.global_context = zhiyoufy_context.get_global_context()

        elk_record = self.handle_context["elk_record"]
        elk_module_root = self.handle_context["elk_module_root"]
        if caller:
            elk_module_root["req"]["caller"] = caller

        if isinstance(req_msg, (dict, str)):
            elk_module_root["req"]["body"] = req_msg

        if root_guid:
            elk_record["root_guid"] = root_guid

    @property
    def elk_record(self):
        return self.handle_context["elk_record"]

    @property
    def elk_module_root(self):
        return self.handle_context["elk_module_root"]

    def log_response_with_elk(self, dumped_response, root_extra=None):
        return RequestHandleUtil.log_response_with_elk(
            dumped_response, handle_context=self.handle_context, root_extra=root_extra)

