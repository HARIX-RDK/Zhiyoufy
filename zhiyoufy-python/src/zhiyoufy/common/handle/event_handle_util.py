from datetime import datetime

from zhiyoufy.common.utils import ElkRecordUtil, TimeUtil, StrUtil
from zhiyoufy.common.models import BaseResultErrorType


def log_event_with_elk(dumped_response, status_code, general_params, root_extra=None):
    logger = general_params["logger"]
    log_prefix = general_params["log_prefix"]
    elk_record = general_params["elk_record"]
    elk_module_root = general_params["elk_module_root"]

    elk_record["timestamp"] = TimeUtil.get_current_time_isoformat()

    start_time = datetime.fromisoformat(elk_module_root["req"]["recv_timestamp"])
    end_time = datetime.fromisoformat(elk_record["timestamp"])
    request_time = int((end_time - start_time).total_seconds() * 1000)
    elk_record["request_time_ms"] = request_time

    if dumped_response:
        for extract_key in ["err_code", "err_msg", "err_detail"]:
            if extract_key in dumped_response:
                elk_record[extract_key] = dumped_response[extract_key]

    if root_extra:
        elk_record.update(root_extra)

    if "err_code" not in elk_record:
        elk_record["err_code"] = int(BaseResultErrorType.RES_OK)
        elk_record["err_msg"] = BaseResultErrorType.RES_OK.name

    if not "rsp" in elk_module_root:
        elk_module_root["rsp"] = {}
    if dumped_response is not None:
        elk_module_root["rsp"]["body"] = dumped_response
    elk_module_root["rsp"]["status_code"] = status_code
    elk_record_prefix = ElkRecordUtil.JSON_EXTRACT_SUFFIX
    log_record = elk_record

    if status_code >= 300:
        log_func = logger.error
    else:
        log_func = logger.info

    log_func("%s Leave log event %s%s" % (log_prefix, elk_record_prefix, StrUtil.pprint(log_record)))
