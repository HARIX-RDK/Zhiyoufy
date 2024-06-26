from jinja2.ext import Extension

from zhiyoufy.common.utils import RandomUtil


class ZhiyoufyExtension(Extension):
    def __init__(self, environment):
        super().__init__(environment)
        environment.globals['RandomUtil'] = RandomUtil
