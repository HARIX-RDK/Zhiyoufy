from .io_util import IOUtil
from .str_util import StrUtil


class MixUtil:
    @staticmethod
    def load_json_then_convert_to_str_then_save(in_path, out_path):
        json_obj = IOUtil.load_json_from_url(in_path)
        json_str = StrUtil.pprint(json_obj)
        wrapped_json_str = StrUtil.pprint(json_str, not_process_str=False)
        IOUtil.save_text_to_path(wrapped_json_str, out_path)
