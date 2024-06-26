import os
import logging
import logging.config
import time
import copy

from robot.output.pyloggingconf import RobotHandler

from zhiyoufy.common.utils import IOUtil

from . import config
from . import zhiyoufy_context

__all__ = ["init_app_base"]

_init_app_base_done = False
_logging_setup_done = False


def setup_logging():
    global _logging_setup_done, log_config

    if _logging_setup_done:
        raise Exception(f"invalid state, _logging_setup_done {_logging_setup_done}")

    active_log_config = copy.deepcopy(log_config)

    config_inst = zhiyoufy_context.get_config_inst()

    _logging_setup_done = True

    log_dir = config_inst.log_dir

    error_file_handler_on = True
    error_file_handler_idx = -1
    file_handler_on = True
    file_handler_idx = -1
    console_on = True

    if "error_file_handler" in config_inst.log_handlers:
        active_log_config["handlers"]["error_file_handler"]["filename"] = os.path.join(log_dir, "app_error.log")
        active_log_config["handlers"]["error_file_handler"]["backupCount"] = config_inst.log_backupCount
    else:
        del active_log_config["handlers"]["error_file_handler"]
        active_log_config["root"]["handlers"].remove("error_file_handler")
        error_file_handler_on = False

    if "file_handler" in config_inst.log_handlers:
        active_log_config["handlers"]["file_handler"]["filename"] = os.path.join(log_dir, "app.log")
        active_log_config["handlers"]["file_handler"]["backupCount"] = config_inst.log_backupCount
    else:
        del active_log_config["handlers"]["file_handler"]
        active_log_config["root"]["handlers"].remove("file_handler")
        file_handler_on = False

    if "console" not in config_inst.log_handlers:
        del active_log_config["handlers"]["console"]
        active_log_config["root"]["handlers"].remove("console")
        console_on = False

    if not (error_file_handler_on or file_handler_on or console_on):
        raise Exception("At least enable one log handler")

    logging.config.dictConfig(active_log_config)
    root_logger = logging.getLogger()

    if config_inst.use_utc_datetime:
        logging.Formatter.converter = time.gmtime

    handler_idx = 0

    if console_on:
        handler_idx = 1

    if file_handler_on:
        file_handler_idx = handler_idx
        handler_idx += 1

    if error_file_handler_on:
        error_file_handler_idx = handler_idx

    if error_file_handler_idx >= 0 and \
            os.path.exists(active_log_config["handlers"]["error_file_handler"]["filename"]):
        if os.stat(active_log_config["handlers"]["error_file_handler"]["filename"]).st_size != 0:
            root_logger.handlers[error_file_handler_idx].doRollover()

    if file_handler_idx >= 0 and \
            os.path.exists(active_log_config["handlers"]["file_handler"]["filename"]):
        root_logger.handlers[file_handler_idx].doRollover()

    if "logger_levels" in config_inst:
        for logger_level in config_inst.logger_levels:
            if logger_level[0] == "root":
                logging.getLogger().setLevel(logger_level[1])
            else:
                logging.getLogger(logger_level[0]).setLevel(logger_level[1])

    logging.debug("App start logging")
    logging.info("App start logging")


def setup_normal_logger():
    normal_logger = logging.getLogger("normal")

    zhiyoufy_context.set_normal_logger(normal_logger)


def setup_robot_logger():
    robot_logger = logging.getLogger("robot")
    robot_handler = RobotHandler()
    robot_logger.addHandler(robot_handler)

    zhiyoufy_context.set_robot_logger(robot_logger)


def init_app_base(config_path, init_app_default_configs=None, check_dir_names=None):
    """解析config，配置app特有的缺省配置，创建缺省目录，配置log"""
    global _init_app_base_done
    log_prefix = "init_app_base:"

    if _init_app_base_done:
        raise Exception(f"invalid state, _init_app_base_done {_init_app_base_done}")

    _init_app_base_done = True

    pid = os.getpid()

    log_msg = f"{log_prefix} pid {pid}, Enter with config_path {config_path}"
    print(log_msg)
    logging.info(log_msg)

    config_file = IOUtil.find_abs_path(config_path)

    if not config_file:
        raise Exception("Can't find config file")

    root_dir = IOUtil.find_root_dir(config_file, check_dir_names=check_dir_names)

    overrides = f"root_dir={root_dir}"
    overrides = overrides.replace("\\", "\\\\")
    config_inst = config.params_from_file(config_file, overrides)

    zhiyoufy_context.set_config_inst(config_inst)

    if init_app_default_configs:
        init_app_default_configs()

    init_base_default_configs(config_inst)

    # Logistics #
    if hasattr(config_inst, "project_dir"):
        IOUtil.maybe_make_dir(config_inst.project_dir)
    if hasattr(config_inst, "log_dir"):
        IOUtil.maybe_make_dir(config_inst.log_dir)
    if hasattr(config_inst, "output_dir"):
        IOUtil.maybe_make_dir(config_inst.output_dir)
    if hasattr(config_inst, "db_dir"):
        IOUtil.maybe_make_dir(config_inst.db_dir)
    if hasattr(config_inst, "data_cache_dir"):
        IOUtil.maybe_make_dir(config_inst.data_cache_dir)

    setup_logging()
    setup_normal_logger()
    setup_robot_logger()

    logging.info("{0:s} Done".format(log_prefix, ))


def init_base_default_configs(config_inst):
    if not hasattr(config_inst, "data_dir"):
        config_inst.data_dir = os.path.join(config_inst.root_dir, "data")

    if not hasattr(config_inst, "log_backupCount"):
        config_inst.log_backupCount = 20

    if not hasattr(config_inst, "log_dir"):
        config_inst.log_dir = os.path.join(config_inst.project_dir, "logs")

    if not hasattr(config_inst, "log_handlers"):
        config_inst.log_handlers = [
            "console", "file_handler", "error_file_handler"]

    if not hasattr(config_inst, "norm_timeout"):
        config_inst.norm_timeout = 10

    if not hasattr(config_inst, "output_dir"):
        config_inst.output_dir = os.path.join(config_inst.project_dir, "outputs")

    if not hasattr(config_inst, "pod_output_dir"):
        config_inst.pod_output_dir = "/output"

    if not hasattr(config_inst, "python_path"):
        config_inst.python_path = [config_inst.root_dir]

    if not hasattr(config_inst, "use_utc_datetime"):
        config_inst.use_utc_datetime = True


log_config = {
    "version": 1,
    "disable_existing_loggers": False,
    "formatters": {
        "brief": {
            "class": "logging.Formatter",
            "format": "%(asctime)s.%(msecs)03d line%(lineno)s %(levelname)s %(message)s",
            "datefmt": "%Y-%m-%d %H:%M:%S"
        },
        "normal": {
            "class": "logging.Formatter",
            "format": "%(asctime)s.%(msecs)03d  %(levelname)-8s [%(name)s] %(module)s:%(funcName)s:%(lineno)d: %(message)s",
            "datefmt": "%Y-%m-%d %H:%M:%S"
        },
        "verbose": {
            "class": "logging.Formatter",
            "format": "%(asctime)s.%(msecs)03d  %(levelname)-8s [%(name)s] %(module)s:%(funcName)s:%(lineno)d: [%(process)d]: %(threadName)s: %(message)s",
            "datefmt": "%Y-%m-%d %H:%M:%S"
        }
    },
    "handlers": {
        "console": {
            "level": "DEBUG",
            "class": "logging.StreamHandler",
            "formatter": "brief",
            "stream": "ext://sys.stdout"
        },
        "file_handler": {
            "class": "logging.handlers.RotatingFileHandler",
            "level": "DEBUG",
            "formatter": "brief",
            "filename": "app.log",
            "maxBytes": 10485760,
            "backupCount": 20,
            "encoding": "utf8"
        },
        "error_file_handler": {
            "class": "logging.handlers.RotatingFileHandler",
            "level": "ERROR",
            "formatter": "verbose",
            "filename": "app_error.log",
            "maxBytes": 10485760,
            "backupCount": 20,
            "encoding": "utf8"
        }
    },
    "loggers": {},
    "root": {
        "handlers": ["console", "file_handler", "error_file_handler"],
        "level": "INFO"
    }
}
