import logging
import uuid
from datetime import datetime
import traceback

from zhiyoufy.common import zhiyoufy_context
from zhiyoufy.common.utils import ElkRecordUtil, TimeUtil, StrUtil

from .base_response import BaseResponse


class RequestHandleUtil:
    @staticmethod
    def build_handle_context(log_prefix, logger_name, elk_record_type, elk_on, guid=None, root_extra=None):
        if root_extra and "guid" in root_extra:
            guid = root_extra["guid"]

        if not guid:
            guid = str(uuid.uuid4())

        if isinstance(logger_name, str):
            logger = logging.getLogger(logger_name)
        else:
            logger = logger_name

        elk_module_id = zhiyoufy_context.get_global_context().elk_module_id
        elk_record = ElkRecordUtil.create_base_elk_record(
            record_type=elk_record_type, guid=guid, module_id=elk_module_id)
        elk_module_root = elk_record[elk_module_id]

        if root_extra:
            elk_record.update(root_extra)

        elk_module_root["req"] = {
            "recv_timestamp": TimeUtil.get_current_time_isoformat()
        }

        trace_records = []

        handle_context = {
            "guid": guid,
            "logger": logger,
            "log_prefix": log_prefix,
            "trace_records": trace_records,
            "elk_on": elk_on,
            "elk_record": elk_record,
            "elk_module_root": elk_module_root,
        }

        return handle_context

    @staticmethod
    def build_base_response(err_enum, handle_context, err_detail=None, exception=None):
        trace_records = handle_context.get("trace_records", None)
        logger = handle_context["logger"]
        log_prefix = handle_context["log_prefix"]

        if exception:
            logger.error("%s: met Exception %s" % (log_prefix, str(exception)))

        response = BaseResponse(err_enum, err_detail=err_detail, trace_records=trace_records)
        dumped_response = response.dump()

        if handle_context["elk_on"]:
            RequestHandleUtil.log_response_with_elk(dumped_response, handle_context)

        return dumped_response

    @staticmethod
    def log_response_with_elk(dumped_response, handle_context, root_extra=None):
        logger = handle_context["logger"]
        log_prefix = handle_context["log_prefix"]
        elk_record = handle_context.get("elk_record", None)
        elk_module_root = handle_context.get("elk_module_root", None)

        has_error = False

        try:
            if elk_record:
                elk_record["timestamp"] = TimeUtil.get_current_time_isoformat()

                start_time = datetime.fromisoformat(elk_module_root["req"]["recv_timestamp"])
                end_time = datetime.fromisoformat(elk_record["timestamp"])
                request_time = int((end_time - start_time).total_seconds() * 1000)
                elk_record["request_time_ms"] = request_time

                extract_root = dumped_response
                if "common_result" in dumped_response:
                    extract_root = dumped_response["common_result"]

                for extract_key in ["err_code", "err_msg", "err_detail"]:
                    if extract_key in extract_root:
                        elk_record[extract_key] = extract_root[extract_key]

                if root_extra:
                    elk_record.update(root_extra)

                if "rsp" not in elk_module_root:
                    elk_module_root["rsp"] = {}

                if dumped_response:
                    elk_module_root["rsp"]["body"] = dumped_response

                elk_record_prefix = ElkRecordUtil.JSON_EXTRACT_SUFFIX
                log_record = elk_record

                if not has_error and "err_code" in elk_record and elk_record["err_code"]:
                    has_error = True
            else:
                elk_record_prefix = ""
                log_record = dumped_response

            log_msg = "%s Req handling done %s%s" % (log_prefix, elk_record_prefix, StrUtil.pprint(log_record))

            if has_error:
                logger.error(log_msg)
            else:
                logger.info(log_msg)
        except Exception as ex:
            err_msg = "%s failed: Exception %s" % (log_prefix, str(ex))
            logger.error(err_msg)
            logger.info(traceback.format_exc())
            raise ex
