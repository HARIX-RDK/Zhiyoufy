import os
import json

from zhiyoufy.common import zhiyoufy_context
from zhiyoufy.common.utils import IOUtil, TemplateLibrary, RandomUtil


class ConfigUtil:
    @staticmethod
    def decode_invisible_config(orig_value, must_exist=True):
        """
        有些配置项含有敏感信息，为了打印log时候不打印它们，
        我们加了一层重定向，如果配置项有指定前缀，则真实
        配置存在重定向对应key下
        """
        config_inst = zhiyoufy_context.get_config_inst()
        invisible_config_prefix = config_inst.invisible_config_prefix

        if orig_value.startswith(invisible_config_prefix):
            to_value = None

            prefix_len = len(invisible_config_prefix)

            config_key = orig_value[prefix_len:]

            if config_key != "" and hasattr(config_inst, config_key):
                to_value = config_inst[config_key]
            elif must_exist:
                raise Exception(f"{config_key} was not configured")

            return to_value
        else:
            return orig_value

    @staticmethod
    def override_config(config, orig_value, key_prefix):
        """
        有些配置项含有敏感信息，为了打印log时候不打印它们，
        我们加了一层重定向，如果配置项有指定前缀，则真实
        配置存在重定向对应key下
        """
        if orig_value.startswith(key_prefix):
            to_value = None

            prefix_len = len(key_prefix)

            config_key = orig_value[prefix_len:]

            if config_key != "" and hasattr(config, config_key):
                to_value = config[config_key]

            return config_key, to_value
        else:
            return None, orig_value

    @staticmethod
    def render_config_if_needed(config, render_context=None, dst_path=None, override_map=None):
        if not config.endswith(".j2"):
            return config
        fixed_prefix = "dynamic_flows_from_tpl/"
        if not config.startswith(fixed_prefix):
            raise Exception("config %s not startswith %s" % (config, fixed_prefix))
        config_inst = zhiyoufy_context.get_config_inst()
        # base_name_orig = config[len(fixed_prefix):]
        if render_context is None:
            render_context = config_inst

        base_name_orig_path = os.path.join(config_inst.data_dir, config.replace(fixed_prefix, "templates/"))
        with open(base_name_orig_path, "r", encoding="utf-8") as fp:
            base_str = fp.read()

        if override_map is not None:
            if isinstance(override_map, str):
                override_map = json.loads(override_map)
            for orig_value, override_value in override_map.items():
                if "random_" in override_value:
                    random_len = int(override_value.split("_")[1])
                    random_value = RandomUtil.random_num_seq(random_len)
                    base_str = base_str.replace(orig_value, random_value)
                else:
                    base_str = base_str.replace(orig_value, override_value)

        if not dst_path:
            if hasattr(config_inst, "rendered_to_output"):
                dst_path = os.path.join(config_inst.output_dir, config[:-3])
            else:
                dst_path = os.path.join(config_inst.data_dir, config[:-3])
        dir_name = os.path.dirname(dst_path)
        IOUtil.maybe_make_dir(dir_name)
        template_library = TemplateLibrary()
        template_library.render(base_str, render_context, dst_path)
        return dst_path
