import time
from datetime import datetime, timezone

from zhiyoufy.common import zhiyoufy_context


class TimeUtil:
    @staticmethod
    def datetime_fromisoformat(datetime_str):
        try:
            if isinstance(datetime_str, datetime):
                return datetime_str

            if datetime_str[-1] == "Z":
                datetime_str = datetime_str[:-1] + "+00:00"

            parsed_datetime = datetime.fromisoformat(datetime_str)
        except Exception as ex:
            return None
        return parsed_datetime
    
    @staticmethod
    def get_current_time_isoformat():
        config_inst = zhiyoufy_context.get_config_inst()

        if config_inst.use_utc_datetime:
            utc_datetime = datetime.utcnow().replace(tzinfo=timezone.utc)
            return utc_datetime.isoformat()
        else:
            local_datetime = datetime.now()
            return local_datetime.isoformat()

    @staticmethod
    def get_current_time_ms():
        millis = int(round(time.time() * 1000))
        return millis

    @staticmethod
    def get_current_time_monotonic():
        return time.monotonic()


if __name__ == "__main__":
    print(TimeUtil.get_current_time_isoformat())
