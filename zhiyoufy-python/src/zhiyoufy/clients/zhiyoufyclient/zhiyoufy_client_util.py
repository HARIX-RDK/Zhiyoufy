import os
import traceback
import concurrent.futures
import json
import functools

from zhiyoufy.common.utils import IOUtil, ConfigUtil, LogUtil, StrUtil

from .zhiyoufy_client_bridge import ZhiyoufyClientBridge


class ZhiyoufyClientUtil(ZhiyoufyClientBridge):
    def __init__(self, parent_client):
        super().__init__(parent_client=parent_client)

    def util_create_config_items_in_batch(self, params):
        log_prefix = "%s %s util_create_config_items_in_batch:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            config_item_type = params["config_item_type"]

            if config_item_type == "zhiyoufy_params_group":
                create_item_func = self.util_create_config_zhiyoufy_params_group_single
            elif config_item_type == "simple_json_params":
                create_item_func = self.util_create_config_simple_json_params_single
            else:
                raise Exception("%s invalid config_item_type %s" % (log_prefix, config_item_type))

            if config_item_type == "simple_json_params":
                value_file = params["value_file"]

                with open(value_file, "r", encoding="utf-8") as fh:
                    simple_json_params_group = json.load(fh)

                create_item_func = functools.partial(create_item_func, simple_json_params_group)

                if "range_end" not in params:
                    range_end = len(simple_json_params_group)
                else:
                    range_end = params["range_end"]
            else:
                range_end = params["range_end"]

            if "range_start" in params:
                cur_idx = params["range_start"]
            else:
                cur_idx = 0

            with concurrent.futures.ThreadPoolExecutor(max_workers=50) as executor:
                parallel_num = 200

                while cur_idx < range_end:
                    if range_end - cur_idx >= parallel_num:
                        cur_round_cnt = parallel_num
                    else:
                        cur_round_cnt = range_end - cur_idx

                    future_to_sub_idx = {}

                    for sub_idx in range(cur_round_cnt):
                        future = executor.submit(create_item_func, params, cur_idx + sub_idx)
                        future_to_sub_idx[future] = sub_idx

                    for future in concurrent.futures.as_completed(future_to_sub_idx):
                        sub_idx = future_to_sub_idx[future]
                        result = future.result()
                        if not result:
                            raise Exception("%s sub_idx %s failed" % (log_prefix, sub_idx))

                    cur_idx += cur_round_cnt
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def util_create_config_zhiyoufy_params_group_single(self, params, cur_idx):
        log_prefix = "%s %s util_create_config_zhiyoufy_params_group_single:" % (
            self.log_prefix, self.zhiyoufy_addr_base)

        try:
            render_context = {}
            for key in ["params_group_id"]:
                render_context[key] = params[key]

            render_context["item_name"] = "%s%04d" % (params["item_name_prefix"], cur_idx)

            config_tpl = "dynamic_flows_from_tpl/100_prepare_for_test/" \
                         "00200_prepare_others/assets/config_zhiyoufy_params_group.conf.j2"
            dst_path = os.path.join(
                self.config_inst.data_dir,
                "dynamic_flows_from_tpl/100_prepare_for_test/"
                "00200_prepare_others/config_zhiyoufy_params_group_%s/%04d.conf" % (
                    params["params_group_id"], cur_idx,))
            ConfigUtil.render_config_if_needed(
                config_tpl, render_context=render_context, dst_path=dst_path)

            config_item_params = {
                "environment_var_path": params["environment_var_path"],
                "config_collection_var_path": params["config_collection_var_path"],
                "name": "%s%04d" % (params["name_prefix"], cur_idx),
                "value_file": dst_path,
            }
            if "update_if_exist" in params:
                config_item_params["update_if_exist"] = params["update_if_exist"]
            self.config_item_client.config_item_create_if_missing_update_if_requested(config_item_params)

            return True
        except Exception as e:
            err_msg = "%s failed: Exception %s" % (log_prefix, str(e))
            self.robot_logger.error(err_msg)
            self.robot_logger.error(traceback.format_exc())
            return False

    def util_create_config_simple_json_params_single(self, simple_json_params_group, params, cur_idx):
        log_prefix = "%s %s util_create_config_simple_json_params_single:" % (
            self.log_prefix, self.zhiyoufy_addr_base)

        try:
            simple_json_params = simple_json_params_group[cur_idx]
            disabled = simple_json_params.pop("disabled", None)

            dir_path = os.path.join(self.config_inst.data_dir,
                                    "dynamic_flows_from_tpl/100_prepare_for_test/config_simple_json_params")
            IOUtil.maybe_make_dir(dir_path)
            dst_path = os.path.join(dir_path, "%04d.json" % (cur_idx,))
            with open(dst_path, "w", encoding="utf-8") as fh:
                json_content = json.dumps(simple_json_params, indent=2)
                lines = json_content.splitlines(keepends=True)
                for line in lines[1:-1]:
                    fh.write(line[2:])

            config_item_params = {
                "environment_var_path": params["environment_var_path"],
                "config_collection_var_path": params["config_collection_var_path"],
                "name": "%s%04d" % (params["name_prefix"], cur_idx),
                "value_file": dst_path,
            }
            if disabled is not None:
                config_item_params["disabled"] = disabled
            if "update_if_exist" in params:
                config_item_params["update_if_exist"] = params["update_if_exist"]
            self.config_item_client.config_item_create_if_missing_update_if_requested(config_item_params)

            return True
        except Exception as e:
            err_msg = "%s failed: Exception %s" % (log_prefix, str(e))
            self.robot_logger.error(err_msg)
            self.robot_logger.error(traceback.format_exc())
            return False
