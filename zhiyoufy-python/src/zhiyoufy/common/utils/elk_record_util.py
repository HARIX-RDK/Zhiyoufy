import uuid
import enum


class ElkRecordUtil:
    JSON_EXTRACT_SUFFIX = "json_extract_prefix"

    @staticmethod
    def create_base_elk_record(record_type, guid=None, req=None, rsp=None, module_id=None):
        elk_record = {}

        if isinstance(record_type, enum.Enum):
            elk_record["type"] = record_type.name
        else:
            elk_record["type"] = record_type

        if guid:
            elk_record["guid"] = guid
        else:
            elk_record["guid"] = str(uuid.uuid4())

        if module_id:
            elk_record[module_id] = {}
            module_root = elk_record[module_id]
        else:
            module_root = elk_record

        if req:
            module_root["req"] = req

        if rsp:
            module_root["rsp"] = rsp

        return elk_record
