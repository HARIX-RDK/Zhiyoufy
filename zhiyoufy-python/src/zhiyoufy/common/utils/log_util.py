import logging
from logging.handlers import RotatingFileHandler
import traceback

from zhiyoufy.common import zhiyoufy_context


class LogUtil:
    @staticmethod
    def log_event_with_err_msg(event_handler, e):
        dumped_response = {
            "err_msg": "Exception: %s" % str(e)
        }
        if hasattr(event_handler, "log_event_with_elk"):
            event_handler.log_event_with_elk(dumped_response, status_code=500)

    @staticmethod
    def log_then_rethrown(log_prefix, e):
        if zhiyoufy_context.get_global_context().in_cleanup:
            zhiyoufy_context.get_robot_logger().warning("Cleanup %s failed: Exception %s" % (log_prefix, str(e)))
            return
        prefix = "CommonUtil"
        if str(e).startswith(prefix):
            raise e
        else:
            err_msg = "%s %s failed: Exception %s" % (prefix, log_prefix, str(e))
            zhiyoufy_context.get_robot_logger().error(err_msg)
            zhiyoufy_context.get_robot_logger().info(traceback.format_exc())
            raise Exception(err_msg)

    @staticmethod
    def set_console_logger_level(lvl):
        root_logger = logging.getLogger()
        for handler in root_logger.handlers:
            if not isinstance(handler, RotatingFileHandler):
                handler.setLevel(lvl)

    @staticmethod
    def set_logger_level(logger_name, level):
        if logger_name == "root":
            logging.getLogger().setLevel(level)
        else:
            logging.getLogger(logger_name).setLevel(level)
