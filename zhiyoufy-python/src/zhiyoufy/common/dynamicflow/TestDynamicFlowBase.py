import faulthandler
import json
import os
import threading
import time
import traceback
from datetime import datetime

from robot.api import SkipExecution

from ..utils import ConfigUtil, StrUtil

from . import GlobalLibraryBase
from .GeneralCommandHandler import GeneralCommandHandler


class TestDynamicFlowBase:
    def __init__(self):
        self.log_prefix = type(self).__name__

        self.global_library_base = GlobalLibraryBase.get_global_library_base()
        self.config_inst = self.global_library_base.config_inst
        self.normal_logger = self.global_library_base.normal_logger
        self.robot_logger = self.global_library_base.robot_logger
        self.global_context = self.global_library_base.global_context

        self.context = TestDynamicFlowContext(test_dynamic_flow_inst=self)
        self.case_context = {}
        self.runner_context = {}
        self.datas_node_block_context_map = {}
        self.finally_node_block_context_map = {}
        self.datas_node_block_context_stack = []
        self.finally_node_block_context_stack = []
        self.handler_map = {}
        self.script_json = None
        self.has_finally_node = False
        self.finally_node_reached = False

        general_handler = GeneralCommandHandler(dynamic_flow_base_inst=self)
        self.add_handler(general_handler)

    def add_handler(self, handler):
        if handler.type_prefix in self.handler_map:
            raise Exception("%s is already registered" % handler.type_prefix)

        self.handler_map[handler.type_prefix] = handler
        handler.setup(self)

    def init_default_config(self):
        if "render_config_but_not_run" not in self.config_inst:
            self.config_inst.render_config_but_not_run = False

    def debug_run(self, test_config, override_map=None):
        try:
            self.run(test_config, override_map=override_map)
        except SkipExecution as ex:
            return

    def run(self, test_config, override_map=None):
        start_time = datetime.now()
        base_test_config = os.path.basename(test_config)
        log_prefix = "%s %s %s:" % (self.log_prefix, "run", base_test_config)

        try:
            self.init_default_config()
            config_path = ConfigUtil.render_config_if_needed(
                test_config, override_map=override_map)
        except Exception as e:
            err_msg = "%s unexpected exception: %s" % (log_prefix, e)
            self.robot_logger.error(err_msg)
            self.robot_logger.info(traceback.format_exc())
            raise e

        try:
            self.global_context.in_cleanup = False

            with open(config_path, "r", encoding="utf-8") as json_file:
                script_json = json.load(json_file)
                self.script_json = script_json

            case_ok_to_run = True
            self.context.params = script_json.get("params", {})
            ignore_case = self.context.params.get("ignore_case", False)
            if ignore_case:
                case_ok_to_run = False
            if case_ok_to_run:
                max_parallel_num = self.context.params.get("max_parallel_num")
                if max_parallel_num and self.config_inst.child_job_idx >= max_parallel_num:
                    self.robot_logger.info("%s: ignore because child_job_idx %s >= %s" % (
                        log_prefix, self.config_inst.child_job_idx, max_parallel_num))
                    case_ok_to_run = False
            if case_ok_to_run and self.config_inst.render_config_but_not_run:
                self.robot_logger.info("%s: ignore because render_config_but_not_run %s" % (
                    log_prefix, self.config_inst.render_config_but_not_run))
                case_ok_to_run = False

            self.robot_logger.info("%s: test_config: %s,\n override_map %s" % (
                log_prefix, test_config, StrUtil.pprint(override_map)))

            if "case_description" in self.context.params:
                self.robot_logger.info("%s: case_description : \n%s" % (
                    log_prefix, StrUtil.pprint(self.context.params["case_description"])))

            if case_ok_to_run:
                self.parse_datas_node_blocks()

                self.parse_finally_node_blocks()

                self.process_datas_node()

                self.process_finally_node()

        except Exception as e:
            if not isinstance(e, SkipExecution):
                err_msg = "%s unexpected exception: %s" % (log_prefix, e)
                self.robot_logger.error(err_msg)
                self.robot_logger.info(traceback.format_exc())
            raise e
        finally:
            try:
                # Only when meeting exception during processing datas node, will self.process_finally_node be executed.
                # And in this case, the exception which occurs due to executing process_finally_node will not
                # be thrown. Instead, the exception occurred in process_datas_node would be thrown.
                self.process_finally_node()
            except Exception as e:
                err_msg = "%s unexpected exception in finally node: %s" % (log_prefix, e)
                self.robot_logger.error(err_msg)
                self.robot_logger.info(traceback.format_exc())
            finally:
                try:
                    self.global_context.in_cleanup = True
                    end_time = datetime.now()
                    duration = end_time - start_time
                    self.robot_logger.info("%s: test duration %s" % (
                        log_prefix, duration))

                    for _, handler in self.handler_map.items():
                        handler.cleanup(self.case_context)

                    for cnt in range(10):
                        time.sleep(1)
                        if threading.active_count() == 1:
                            break

                    if threading.active_count() > 1:
                        faulthandler.dump_traceback()
                        self.normal_logger.error("%s: threading active_count %d larger than 1" % (
                            log_prefix, threading.active_count()))

                    self.robot_logger.info("%s: finally block end" % (
                        log_prefix,))
                except Exception as e:
                    err_msg = "%s unexpected exception: %s" % (log_prefix, e)
                    self.robot_logger.info(err_msg)
                    self.robot_logger.info(traceback.format_exc())

    def parse_node_blocks(self, node_datas, node_block_context_map, log_prefix):
        self.robot_logger.info("%s: parse_node_blocks begins" % (log_prefix,))

        block_context_list = []
        block_name_set = set()

        for command_idx in range(len(node_datas)):
            command = node_datas[command_idx]

            try:
                for type_prefix, handler in self.handler_map.items():
                    if not command['type'].startswith(type_prefix):
                        continue

                    if not hasattr(handler, "is_block_begin"):
                        break

                    if handler.is_block_begin(command["type"]):
                        block_name = command["block_name"]

                        if block_name in block_name_set:
                            raise Exception("block_name %s is used more than once" % block_name)

                        block_name_set.add(block_name)

                        block_context = {
                            "block_name": block_name,
                            "begin_idx": command_idx,
                            "begin_command": command,
                        }

                        block_context_list.append(block_context)
                    elif handler.is_block_end(command["type"]):
                        if len(block_context_list) == 0:
                            raise Exception("Unmatched block end")

                        last_block_context = block_context_list.pop(len(block_context_list) - 1)

                        block_name = command["block_name"]

                        if last_block_context["block_name"] != block_name:
                            raise Exception("Unmatched block name")

                        last_block_context.update({
                            "end_idx": command_idx,
                            "end_command": command,
                        })

                        node_block_context_map[block_name] = last_block_context

                    break
            except Exception as ex:
                self.robot_logger.error("%s: met exception when parse command_idx %s, command %s" % (
                    log_prefix, command_idx, StrUtil.pprint(command)))

                raise ex

        if len(block_context_list) > 0:
            raise Exception("block %s is incomplete" % block_context_list[0]["block_name"])

        self.robot_logger.info("%s: parse_node_blocks ends" % (log_prefix,))

    def parse_datas_node_blocks(self):
        log_prefix = "parse_datas_node_blocks"
        self.parse_node_blocks(self.script_json["datas"], self.datas_node_block_context_map, log_prefix)

    def parse_finally_node_blocks(self):
        log_prefix = "parse_finally_node_blocks"

        if "finally_datas" in self.script_json:
            self.parse_node_blocks(self.script_json["finally_datas"], self.finally_node_block_context_map, log_prefix)
            self.has_finally_node = True

    def process_node(self, node_datas, log_prefix):
        self.robot_logger.info("%s: process_node begins" % (log_prefix,))

        idx = 0
        while idx < len(node_datas):
            self.context.cur_idx = idx
            data = node_datas[idx]

            if "ignore" in data and data["ignore"]:
                self.robot_logger.info("%s: ignore process %s : \n%s" % (
                    log_prefix, idx, StrUtil.pprint(data)))
                idx += 1
                continue
            else:
                self.robot_logger.info("%s: to process %s : \n%s" % (
                    log_prefix, idx, StrUtil.pprint(data)))

            self.process_command(data)

            if self.context.override_idx is not None:
                idx = self.context.override_idx
                self.context.override_idx = None
            else:
                idx += 1

        self.robot_logger.info("%s: process_node ends" % (log_prefix,))

    def process_datas_node(self):
        log_prefix = "process_datas_node"

        self.process_node(self.script_json["datas"], log_prefix)

    def process_finally_node(self):
        log_prefix = "process_finally_node"

        if self.has_finally_node and self.finally_node_reached is False:
            self.finally_node_reached = True
            self.process_node(self.script_json["finally_datas"], log_prefix)

    def process_command(self, command):
        log_prefix = "process_command"

        handled = False

        try:
            for type_prefix, handler in self.handler_map.items():
                if command['type'].startswith(type_prefix):
                    handler.process(command)
                    handled = True
                    break

            if not handled:
                raise Exception("unknown type %s" % command['type'])
        except Exception as ex:
            if not isinstance(ex, SkipExecution):
                self.robot_logger.error("%s: met exception when handle command %s" % (
                    log_prefix, StrUtil.pprint(command)))

            raise ex


class TestDynamicFlowContext:
    def __init__(self, test_dynamic_flow_inst):
        self.override_idx = None
        self.cur_idx = None

        self.loop_map = {}

        self.global_library_base = GlobalLibraryBase.get_global_library_base()
        self.test_dynamic_flow_inst = test_dynamic_flow_inst

        self.lib_client_map = {}
        self.lib_client_factory_map = {}

    def add_lib_client_factory(self, lib_client_name, lib_client_factory):
        self.lib_client_factory_map[lib_client_name] = lib_client_factory

    def get_lib_client(self, lib_client_name):
        if lib_client_name in self.lib_client_map:
            return self.lib_client_map[lib_client_name]
        if lib_client_name in self.lib_client_factory_map:
            self.lib_client_map[lib_client_name] = self.lib_client_factory_map[lib_client_name]()
            return self.lib_client_map[lib_client_name]

    def get_step_var(self, step_var_path, raise_exception_if_miss=True):
        return self.global_library_base.get_step_var(step_var_path, raise_exception_if_miss)

    def set_step_var(self, step_var_path, step_var_value):
        self.global_library_base.set_step_var(step_var_path, step_var_value)
