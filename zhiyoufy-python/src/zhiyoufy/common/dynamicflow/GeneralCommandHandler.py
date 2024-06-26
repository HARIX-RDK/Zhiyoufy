import copy
import os
import random
import shutil
import time
from datetime import datetime, timezone, timedelta
import json

from ..utils import TemplateLibrary, CollectionUtil, StrUtil, IOUtil, TimeUtil

from .BlockContextBase import BlockContextBase
from .WhileLoopBlockContext import WhileLoopBlockContext
from .CommandHandlerBase import CommandHandlerBase
from .ForLoopBlockContext import ForLoopBlockContext


class GeneralCommandHandler(CommandHandlerBase):
    def __init__(self, dynamic_flow_base_inst):
        super().__init__(dynamic_flow_base_inst)

        self.type_prefix = "general_"
        self.boolean_type = bool.__name__
        self.str_type = str.__name__
        self.int_type = int.__name__

    def is_block_begin(self, command_type):
        return command_type in ["general_for_loop_begin", "general_if_condition_begin",
                                "general_while_loop_begin"]

    def is_block_end(self, command_type):
        return command_type in ["general_for_loop_end", "general_if_condition_end",
                                "general_while_loop_end"]

    def process(self, data):
        if data['type'].startswith('general_'):
            self.process_type_of_general(data)
        else:
            raise Exception("Unknown general type %s" % data['type'])

    def process_type_of_general(self, data):
        if data['type'] == 'general_case_comments':
            return
        elif data['type'] == 'general_case_description':
            return
        elif data['type'] == 'general_check_step_result':
            self.process_type_general_check_step_result(data)
        elif data['type'] == 'general_compose_then_run':
            self.process_type_general_compose_then_run(data)
        elif data['type'] == 'general_download_and_copy_file_if_needed':
            self.process_type_general_download_and_copy_file_if_needed(data)
        elif data['type'] == 'general_for_loop_begin':
            self.process_type_general_for_loop_begin(data)
        elif data['type'] == 'general_for_loop_end':
            self.process_type_general_for_loop_end(data)
        elif data['type'] == 'general_if_condition_begin':
            self.process_type_general_if_condition_begin(data)
        elif data['type'] == 'general_if_condition_end':
            self.process_type_general_if_condition_end(data)
        elif data['type'] == 'general_loop_begin':
            self.process_type_general_loop_begin(data)
        elif data['type'] == 'general_loop_end':
            self.process_type_general_loop_end(data)
        elif data['type'] == 'general_print_step_var':
            self.process_type_general_print_step_var(data)
        elif data['type'] == 'general_set_case_context':
            self.process_type_general_set_case_context(data)
        elif data['type'] == 'general_set_step_var':
            self.process_type_general_set_step_var(data)
        elif data['type'] == 'general_set_step_var_from_json_url':
            self.process_type_general_set_step_var_from_json_url(data)
        elif data['type'] == 'general_skip_case_if_condition':
            self.process_type_general_skip_case_if_condition(data)
        elif data['type'] == 'general_sleep':
            self.process_type_general_sleep(data)
        elif data['type'] == 'general_update_step_var':
            self.process_type_general_update_step_var(data)
        elif data['type'] == 'general_while_loop_begin':
            self.process_type_general_while_loop_begin(data)
        elif data['type'] == 'general_while_loop_end':
            self.process_type_general_while_loop_end(data)
        elif data['type'] == 'general_save_round_result':
            self.process_type_general_save_round_result(data)
        else:
            raise Exception("Unknown general type %s" % data['type'])

    # region handlers

    def process_type_general_check_step_result(self, data):
        result_var_path_list = data["result_var_path_list"] if "result_var_path_list" in data else None
        if result_var_path_list:
            for i, result_var_path in enumerate(result_var_path_list):
                result_var = self.context.get_step_var(result_var_path, False)
                if result_var is not None:
                    raise result_var

    def process_type_general_compose_then_run(self, data):
        mode = data.get("mode", "override")
        parts = data["parts"]
        composite = None

        if not mode in ["override", "merge"]:
            raise Exception("invalid mode %s" % mode)

        for part in parts:
            if "part_var_path" in part:
                part_var_keys = part.get("part_var_keys", None)
                part_whole = self.get_step_var(part["part_var_path"])

                if not part_var_keys:
                    part = part_whole
                else:
                    part = {}
                    for part_var_key in part_var_keys:
                        if isinstance(part_var_key, str):
                            part[part_var_key] = part_whole[part_var_key]
                        elif isinstance(part_var_key, list):
                            part[part_var_key[1]] = part_whole[part_var_key[0]]
                        else:
                            raise Exception(f"Unexpected part_var_key type {type(part_var_key)}")

            if composite is None:
                composite = copy.deepcopy(part)
            else:
                CollectionUtil.dict_update_on_mode(composite, part, mode)

        self.robot_logger.info("compose_then_run: composite \n%s" % (
            StrUtil.pprint(composite)))

        self.process_command(composite)

    def process_type_general_download_and_copy_file_if_needed(self, data):
        src_url = data["src_url"]
        timeout = data.get("timeout", 200)

        local_abs_file_path = IOUtil.get_local_abs_file_path(src_url, timeout)

        base_file_name = os.path.basename(local_abs_file_path)

        target_base = data.get("target_base", self.config_inst.data_dir)
        target_dirs = data["target_dirs"]
        for target_dir in target_dirs:
            target_dir_path = os.path.join(target_base, target_dir)
            if not os.path.exists(target_dir_path):
                os.makedirs(target_dir_path)

            target_file_path = os.path.join(target_dir_path, base_file_name)
            if not os.path.exists(target_file_path):
                shutil.copyfile(local_abs_file_path, target_file_path)

    def process_type_general_for_loop_begin(self, data):
        block_name = data["block_name"]

        last_block_context = self.get_last_block_context()

        if last_block_context is None or last_block_context.block_name != block_name:
            self.robot_logger.info("general_for_loop_begin: begin for block_name %s" % (block_name,))

            last_block_context = ForLoopBlockContext(self.block_context_map[block_name])
            self.block_context_stack.append(last_block_context)

            if "range_end" in data:
                last_block_context.for_type = "range_end"
                last_block_context.range_end = data["range_end"]

                if "range_begin" in data:
                    last_block_context.range_begin = data["range_begin"]
                else:
                    last_block_context.range_begin = 0

                last_block_context.iter_next_idx = 0

                if "iter_step" in data:
                    last_block_context.iter_step = data["iter_step"]
                else:
                    last_block_context.iter_step = 1

                last_block_context.loop_cnt = \
                    len(list(range(last_block_context.range_begin,
                                   last_block_context.range_end, last_block_context.iter_step)))
            elif "in_values" in data:
                last_block_context.for_type = "in_values"
                last_block_context.in_values = data["in_values"]
                last_block_context.iter_next_idx = 0
                last_block_context.loop_cnt = len(last_block_context.in_values)
            elif "in_values_var_path" in data:
                in_values = self.get_step_var(data["in_values_var_path"])
                start_index = data.get("start_index", 0)
                end_index = data.get("end_index", -1)
                last_block_context.for_type = "in_values"
                if end_index != -1:
                    last_block_context.in_values = in_values[start_index:end_index + 1]
                else:
                    last_block_context.in_values = in_values[start_index:]

                last_block_context.iter_next_idx = 0
                last_block_context.loop_cnt = len(last_block_context.in_values)
            else:
                raise Exception("invalid for loop type, specify one of range_end, in_values")

        if last_block_context.for_type == "range_end":
            last_block_context.iter_next_value = last_block_context.range_begin + \
                                                 last_block_context.iter_next_idx * last_block_context.iter_step

            if last_block_context.iter_next_value >= last_block_context.range_end:
                self.robot_logger.info("block end for block_name %s" % (block_name,))
                last_block_context.block_end = True
                self.context.override_idx = last_block_context.end_idx
                return
        elif last_block_context.for_type == "in_values":
            if last_block_context.iter_next_idx >= len(last_block_context.in_values):
                self.robot_logger.info("block end for block_name %s" % (block_name,))
                last_block_context.block_end = True
                self.context.override_idx = last_block_context.end_idx
                return

            last_block_context.iter_next_value = last_block_context.in_values[last_block_context.iter_next_idx]
        else:
            raise Exception("Unexpected")

        disabled = data.get("disabled", False)

        if disabled:
            self.robot_logger.info("disabled: block end for block_name %s" % (block_name,))
            last_block_context.block_end = True
            self.context.override_idx = last_block_context.end_idx
            return

        self.robot_logger.info("general_for_loop_begin: iter for block_name %s, next_idx %s/%s, next_value %s" % (
            block_name, last_block_context.iter_next_idx, last_block_context.loop_cnt,
            last_block_context.iter_next_value))

        if "next_value_var_path" in data:
            self.set_step_var(data["next_value_var_path"], last_block_context.iter_next_value)

    def process_type_general_for_loop_end(self, data):
        block_name = data["block_name"]

        last_block_context = self.get_last_block_context()

        if last_block_context.block_name != block_name:
            raise Exception

        if last_block_context.block_end:
            self.pop_last_block_context()

            self.robot_logger.info("general_for_loop_begin: end for block_name %s" % (block_name,))

            return

        last_block_context.iter_next_idx += 1
        self.context.override_idx = last_block_context.begin_idx

    def process_type_general_if_condition_begin(self, data):
        block_name = data["block_name"]

        return_if_true = data.get("return_if_true", True)

        if "condition_value" in data:
            condition = data["condition_value"]
        else:
            operator = data["operator"]
            if not operator:
                raise Exception("general_if_condition_loop_begin: operator is None")

            var_type = data["var_type"]
            if not var_type:
                raise Exception("general_if_condition_loop_begin: var_type is None")

            argument_var_path = data["argument_var_path"]
            if not argument_var_path:
                raise Exception("general_if_condition_loop_begin: argument_var_path is None")

            argument_var = self.get_step_var_if_match_type(var_type, argument_var_path)

            target_argument_var_path = data["target_argument_var_path"]
            if not target_argument_var_path:
                raise Exception("general_if_condition_loop_begin: target_argument_var_path is None")

            target_argument_var = self.get_step_var_if_match_type(var_type, target_argument_var_path)

            condition = self.evaluate(operator, var_type, argument_var, target_argument_var)

        if return_if_true is False:
            condition = not condition

        last_block_context = BlockContextBase(self.block_context_map[block_name])
        self.block_context_stack.append(last_block_context)

        if not condition:
            self.robot_logger.info("block end for block_name %s" % (block_name,))
            last_block_context.block_end = True
            self.context.override_idx = last_block_context.end_idx
            return

    def process_type_general_if_condition_end(self, data):
        block_name = data["block_name"]

        last_block_context = self.get_last_block_context()

        if last_block_context.block_name != block_name:
            raise Exception

        self.pop_last_block_context()

        self.robot_logger.info("general_if_condition_end: end for block_name %s" % (block_name,))

    def process_type_general_loop_begin(self, data):
        loop_id = data["loop_id"]
        self.context.loop_map[loop_id] = {
            "loop_cnt": data["loop_cnt"],
            "loop_seq": 0,
            "loop_begin_idx": self.context.cur_idx
        }

        self.robot_logger.info("general_loop: loop_id %s loop_seq %s" % (loop_id, 0))

    def process_type_general_loop_end(self, data):
        loop_info = self.context.loop_map[data["loop_id"]]

        loop_info["loop_seq"] += 1

        if loop_info["loop_seq"] < loop_info["loop_cnt"]:
            self.context.override_idx = loop_info["loop_begin_idx"] + 1

        self.robot_logger.info("general_loop: loop_id %s loop_seq %s" % (
            data["loop_id"], loop_info["loop_seq"]))

    def process_type_general_print_step_var(self, data):
        step_var_path = data["step_var_path"]
        step_var_value = self.get_step_var(step_var_path, False)

        if step_var_value is not None and type(step_var_value) is dict:
            self.robot_logger.info("print_step_Var: step_var_path is:%s, step_var_value is:\n%s" % (
                step_var_path, json.dumps(step_var_value, indent=4)))
            return

        self.robot_logger.info("print_step_Var: step_var_path is:%s, step_var_value is:\n%s" % (
            step_var_path, step_var_value))

    def process_type_general_set_case_context(self, context_params):
        self.case_context = context_params["context"]

    def process_type_general_set_step_var(self, data):
        step_var_path = data["step_var_path"]

        step_var_value = data.get("step_var_value", None)
        step_var_value_from_var_path = data.get("step_var_value_from_var_path", None)
        step_var_value_generator = data.get("step_var_value_generator", None)

        if step_var_value is None and step_var_value_from_var_path is None and step_var_value_generator is None:
            self.set_step_var(step_var_path, None)

        elif step_var_value is not None:
            self.set_step_var(step_var_path, step_var_value)

        elif step_var_value_from_var_path is not None:
            step_var_value = self.get_step_var(step_var_value_from_var_path)
            self.set_step_var(step_var_path, step_var_value)

        elif step_var_value_generator is not None:
            if step_var_value_generator == "current_time":
                cur_time = datetime.now().astimezone(timezone(timedelta(hours=8)))
                self.set_step_var(step_var_path, cur_time.strftime("%Y-%m-%d %H:%M:%S %Z"))

            elif step_var_value_generator == "concatenate_var_and_value":
                var_path = data["var_path"]
                var_value = self.get_step_var(var_path)
                value = data["value"]
                self.set_step_var(step_var_path, f"{var_value}{value}")
            elif step_var_value_generator == "current_timestamp_ms":
                cur_time_ts_ms = TimeUtil.get_current_time_ms()
                self.set_step_var(step_var_path, cur_time_ts_ms)
            else:
                raise Exception("unsupported step_var_value_generator: %s" % (step_var_value_generator,))

    def process_type_general_set_step_var_from_json_url(self, data):
        step_var_path = data["step_var_path"]
        json_url = data.get("json_url", None)
        render_template_first = data.get("render_template_first", False)

        if json_url:
            if not json_url.startswith("http"):
                json_url = os.path.join(self.config_inst.data_dir, json_url)
        else :
            json_url = data["json_file_url"]

        text_content = IOUtil.load_text_from_url(json_url)

        if render_template_first:
            template_library = TemplateLibrary()
            text_content = template_library.render(text_content, self.config_inst)

        json_content = json.loads(text_content)

        self.set_step_var(step_var_path, json_content)

        self.robot_logger.info("set_step_var_from_json_url: step_var_path %s json_content \n%s" % (
            step_var_path, StrUtil.pprint(json_content)))

    def process_type_general_skip_case_if_condition(self, data):
        condition = data["condition"]

        if condition:
            from robot.api import SkipExecution

            self.robot_logger.info("skip case as condition match")
            raise SkipExecution('skip case as condition match')

    def process_type_general_sleep(self, data):
        if "time_seconds" in data:
            time.sleep(data["time_seconds"])
        elif "time_seconds_min" in data:
            time_seconds_min = data["time_seconds_min"]
            time_seconds_max = data["time_seconds_max"]
            time_seconds = round(time_seconds_min + random.random() * (time_seconds_max - time_seconds_min), 2)
            self.robot_logger.info(f"to sleep time_seconds {time_seconds}")
            time.sleep(time_seconds)
        else:
            raise Exception("general_sleep: invalid params")

    def process_type_general_update_step_var(self, data):
        step_var_path = data["step_var_path"]
        if not step_var_path:
            raise Exception("general_update_step_var: step_var_path is None")

        operator = data["operator"]
        if not operator:
            raise Exception("general_update_step_var: operator is None")

        var_type = data["var_type"]
        if not operator:
            raise Exception("general_update_step_var: var_type is None")

        operand = data["operand"]
        if not operator:
            raise Exception("general_update_step_var: operand is None")

        updated_value = self.get_value_and_evaluate(step_var_path, operator, var_type, operand)

        self.robot_logger.info(f"update {step_var_path} to {updated_value}")

        self.set_step_var(step_var_path, updated_value)

    def process_type_general_while_loop_begin(self, data):
        block_name = data["block_name"]

        operator = data["operator"]
        if not operator:
            raise Exception("general_while_loop_begin: operator is None")

        var_type = data["var_type"]
        if not var_type:
            raise Exception("general_while_loop_begin: var_type is None")

        return_if_true = data["return_if_true"] if "return_if_true" in data else True

        argument_var_path = data["argument_var_path"]
        if not argument_var_path:
            raise Exception("general_while_loop_begin: argument_var_path is None")

        argument_var = self.get_step_var_if_match_type(var_type, argument_var_path)

        target_argument_var_path = data["target_argument_var_path"]
        if not target_argument_var_path:
            raise Exception("general_while_loop_begin: target_argument_var_path is None")

        target_argument_var = self.get_step_var_if_match_type(var_type, target_argument_var_path)

        condition = self.evaluate(operator, var_type, argument_var, target_argument_var)

        if return_if_true is False:
            condition = not condition

        last_block_context = self.get_last_block_context()

        if last_block_context is None or last_block_context.block_name != block_name:
            self.robot_logger.info("general_while_loop_begin: begin for block_name %s" % (block_name,))

            last_block_context = WhileLoopBlockContext(self.block_context_map[block_name])

            last_block_context.var_path = data["argument_var_path"]
            last_block_context.target_var_path = data["target_argument_var_path"]
            last_block_context.operator = data["operator"]
            last_block_context.var_type = data["var_type"]

            self.block_context_stack.append(last_block_context)

        if not condition:
            self.robot_logger.info("block end for block_name %s" % (block_name,))
            last_block_context.block_end = True
            self.context.override_idx = last_block_context.end_idx
            return

    def process_type_general_while_loop_end(self, data):
        block_name = data["block_name"]

        last_block_context = self.get_last_block_context()

        if last_block_context.block_name != block_name:
            raise Exception

        if last_block_context.block_end:
            self.pop_last_block_context()

            self.robot_logger.info("general_while_loop_end: end for block_name %s" % (block_name,))

            return

        self.context.override_idx = last_block_context.begin_idx

    def process_type_general_save_round_result(self, data):

        round_id_var_path = data["round_id_var_path"]
        round_id = self.get_step_var(round_id_var_path)

        single_round_result_var_path = data["single_round_result_var_path"]
        single_round_result = self.get_step_var(single_round_result_var_path)

        multi_rounds_result_var_path = data["multi_rounds_result_var_path"]
        multi_rounds_result = self.get_step_var(multi_rounds_result_var_path, raise_exception_if_miss=False)

        if multi_rounds_result is None:
            multi_rounds_result = {}

        multi_rounds_result[str(round_id)] = single_round_result

        self.set_step_var(multi_rounds_result_var_path, multi_rounds_result)

    def get_step_var_if_match_type(self, var_type, step_var_path):

        step_var = self.get_step_var(step_var_path)

        if step_var is None:
            raise Exception("value of step_var_path %s is %s" % (step_var_path, step_var))

        step_var_type = self.get_value_type_name(step_var)

        if var_type in [self.int_type, self.boolean_type, self.str_type]:
            if step_var_type == var_type:
                return step_var

        raise Exception("step_var [%s] type [%s] not matched with the specified var_type [%s]"
                        % (step_var, step_var_type, var_type))

    def evaluate(self, operator, var_type, arg1, arg2):
        if var_type == self.boolean_type:
            return self.evaluate_boolean_expr(operator, arg1, arg2)

        if var_type == self.int_type:
            return self.evaluate_int_expr(operator, arg1, arg2)

        if var_type == self.str_type:
            return self.evaluate_str_expr(operator, arg1, arg2)

        raise Exception("evaluate, unsupported var_type %s" % (var_type,))

    @staticmethod
    def evaluate_boolean_expr(operator, arg1, arg2):
        if operator == "==":
            return arg1 == arg2
        elif operator == "!=":
            return arg1 != arg2

        raise Exception("evaluate, unsupported operator %s for boolean type" % (operator,))

    @staticmethod
    def evaluate_int_expr(operator, arg1, arg2):
        if operator == "==":
            return arg1 == arg2
        elif operator == "!=":
            return arg1 != arg2
        elif operator == "<":
            return arg1 < arg2
        elif operator == "<=":
            return arg1 <= arg2
        elif operator == ">":
            return arg1 > arg2
        elif operator == ">=":
            return arg1 >= arg2
        elif operator == "+":
            return arg1 + arg2
        elif operator == "-":
            return arg1 - arg2
        elif operator == "*":
            return arg1 * arg2
        elif operator == "/":
            return arg1 / arg2

        raise Exception("evaluate, unsupported operator %s for int type" % (operator,))

    @staticmethod
    def evaluate_str_expr(operator, arg1, arg2):
        if operator == "==":
            return arg1 == arg2
        elif operator == "!=":
            return arg1 != arg2

        raise Exception("evaluate, unsupported operator %s for str type" % (operator,))

    def get_value_and_evaluate(self, step_var_path, operator, var_type, operand):
        orig_value = self.get_step_var_if_match_type(var_type, step_var_path)

        return self.evaluate(operator, var_type, orig_value, operand)

    @staticmethod
    def get_value_type_name(value):
        return type(value).__name__

    # endregion
