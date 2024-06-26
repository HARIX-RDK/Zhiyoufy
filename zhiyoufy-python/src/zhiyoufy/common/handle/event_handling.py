import uuid
import logging

from zhiyoufy.common import zhiyoufy_context
from zhiyoufy.common.utils import ElkRecordUtil, TimeUtil


def event_handling_setup(log_prefix, logger_name, elk_record_type, guid=None, root_extra=None):
    if root_extra and "guid" in root_extra:
        guid = root_extra["guid"]
    if not guid:
        guid = str(uuid.uuid4())

    if not isinstance(logger_name, str):
        logger = logger_name
    else:
        logger = logging.getLogger(logger_name)

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

    general_params = {
        "guid": guid,
        "logger": logger,
        "log_prefix": log_prefix,
        "trace_records": trace_records,
        "elk_record": elk_record,
        "elk_module_root": elk_module_root,
    }

    return general_params
