import math
import json
from datetime import datetime

from zhiyoufy.common import zhiyoufy_context
from zhiyoufy.common.utils import StrUtil


class CheckUtil:
    @staticmethod
    def assert_for_log(condition, error_message):
        assert condition, error_message

    @staticmethod
    def check_arg_name(args, name_dict={}):
        """ Raise error if obsolete arg names are present. """
        # Mapping - key: old name, value: new name
        for old_name, new_name in name_dict.items():
            CheckUtil.assert_for_log(old_name not in args,
                                     f"Error: Attempting to load old arg name [{old_name}], "
                                     f"please update to new name [{name_dict[old_name]}]")

    @staticmethod
    def check_arg_required(args, required_list=[]):
        """ Raise error if required but not set. """
        for arg_name in required_list:
            CheckUtil.assert_for_log(arg_name in args, f"Error: Arg [{arg_name}] is required but not set")

    @staticmethod
    def check_dict_expected(check_fields, expected, other, ignore_order=False, order_by_key=None):
        if not check_fields:
            return True

        other_value = None
        list_idx = None

        for key, value in check_fields.items():
            local_value = expected.get(key)
            keys = key.split(".")
            for idx, sub_key in enumerate(keys):
                list_idx_start = sub_key.find("[")

                if list_idx_start >= 0:
                    list_idx_end = sub_key.find("]")
                    list_idx = int(sub_key[list_idx_start + 1:list_idx_end])
                    sub_key = sub_key[:list_idx_start]

                if idx == 0:
                    other_value = other.get(sub_key, None)
                else:
                    if other_value is None:
                        zhiyoufy_context.get_robot_logger().info("other doesn't have valid value for key %s, other \n%s" % (
                            key, StrUtil.pprint(other)))
                        return False

                    if type(other_value) is str:
                        other_value = json.loads(other_value)

                    other_value = other_value.get(sub_key, None)

                if list_idx_start >= 0:
                    if other_value is None:
                        zhiyoufy_context.get_robot_logger().info("other doesn't have valid value for key %s, other \n%s" % (
                            key, StrUtil.pprint(other)))
                        return False

                    if not isinstance(other_value, list):
                        zhiyoufy_context.get_robot_logger().info("other key %s is not list, other \n%s" % (
                            key, StrUtil.pprint(other)))
                        return False
                    if len(other_value) < list_idx:
                        zhiyoufy_context.get_robot_logger().info("other key %s not contain idx %s, other \n%s" % (
                            key, list_idx, StrUtil.pprint(other)))
                        return False
                    other_value = other_value[list_idx]

            if other_value is None and local_value is None:
                return True
            elif other_value is None or local_value is None:
                return False

            if not isinstance(value, dict):
                if isinstance(value, list) and ignore_order:
                    if isinstance(local_value[0], dict):
                        local_value = sorted(local_value, key=order_by_key)
                        other_value = sorted(other_value, key=order_by_key)
                    else:
                        local_value = sorted(local_value)
                        other_value = sorted(other_value)

                if local_value != other_value:
                    zhiyoufy_context.get_robot_logger().info("key %s not match, expect %s, got %s, other \n%s" % (
                        key, local_value, other_value, StrUtil.pprint(other)
                    ))
                    return False
                continue

            for inner_key in value.keys():
                inner_local_value = local_value.get(inner_key)
                inner_other_value = other_value.get(inner_key, None)

                if isinstance(inner_local_value, list) \
                        and isinstance(inner_other_value, list) \
                        and ignore_order:
                    if isinstance(inner_local_value[0], dict):
                        inner_local_value = sorted(inner_local_value, key=order_by_key)
                        inner_other_value = sorted(inner_other_value, key=order_by_key)
                    else:
                        inner_local_value = sorted(inner_local_value)
                        inner_other_value = sorted(inner_other_value)

                if inner_local_value != inner_other_value:
                    zhiyoufy_context.get_robot_logger().info("key %s.%s not match, expect %s, got %s" % (
                        key, inner_key, inner_local_value,
                        inner_other_value
                    ))
                    return False

        return True

    @staticmethod
    def check_json(input_str):
        try:
            json.loads(input_str)
            return True
        except:
            return False

    @staticmethod
    def equal(lhs, rhs):
        if isinstance(lhs, float) and isinstance(rhs, float):
            return math.isclose(lhs, rhs, abs_tol=0.0001)
        elif lhs == rhs:
            return True
        return False

    @staticmethod
    def halt(description="halt"):
        zhiyoufy_context.get_robot_logger().error("Enter halt: %s" % description)

        raise Exception(description)

    @staticmethod
    def raise_exception_for_bad_response(log_prefix, r):
        if r is not None:
            err_msg = "%s failed, r.status_code %s, response: %s" % (log_prefix, r.status_code, r.text)
            raise Exception(err_msg)
        else:
            err_msg = "%s failed, r.status_code %s, no response" % (log_prefix, r.status_code)
            raise Exception(err_msg)

    @staticmethod
    def validate_and_return_json_rsp(log_prefix, r, request=None, trace_on=True, valid_codes=[0], traces=None):
        if 200 <= r.status_code < 300:
            trace_msg = "%s http ok" % log_prefix
            if trace_on:
                zhiyoufy_context.get_robot_logger().info(trace_msg)
            elif traces is not None:
                traces.append("UTC %s %s" % (datetime.utcnow(), trace_msg))
            call_rsp = r.json()
            trace_msg = "%s response: \n%s" % (log_prefix, StrUtil.pprint(call_rsp))
            if trace_on:
                zhiyoufy_context.get_robot_logger().info(trace_msg)
            elif traces is not None:
                traces.append("UTC %s %s" % (datetime.utcnow(), trace_msg))
            if valid_codes is not None:
                CheckUtil.validate_response(call_rsp, log_prefix, request, trace_on, valid_codes, traces)
            return call_rsp
        else:
            CheckUtil.raise_exception_for_bad_response(log_prefix, r)

    @staticmethod
    def validate_response(call_rsp, log_prefix, request=None, trace_on=True, valid_codes=[0], traces=None):
        if 'code' in call_rsp:
            err_code = int(call_rsp['code'])
        elif 'err_code' in call_rsp:
            err_code = int(call_rsp['err_code'])
        elif 'error' in call_rsp:
            err_code = int(call_rsp['error']['code'])
        else:
            err_code = None

        if err_code is not None and err_code not in valid_codes:
            err_msg = "code is not one of %s, but is %s" % (valid_codes, err_code,)
            if request:
                err_msg = "%s, request: %s" % (err_msg, request)
            raise Exception(err_msg)
        else:
            trace_msg = "%s: internal code ok" % log_prefix
            if trace_on:
                zhiyoufy_context.get_robot_logger().info(trace_msg)
            elif traces is not None:
                traces.append("UTC %s %s" % (datetime.utcnow(), trace_msg))
