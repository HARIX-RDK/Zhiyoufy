from jinja2 import Environment, PackageLoader, select_autoescape

from zhiyoufy.common import zhiyoufy_context


class TemplateLibrary:
    def __init__(self, package_name="data"):
        self.loader = PackageLoader(package_name)

        config_inst = zhiyoufy_context.get_config_inst()

        if hasattr(config_inst, "jinja2_extensions"):
            jinja2_extensions = config_inst.jinja2_extensions
        else:
            # 'jinja2.ext.do', 'jinja2_time.TimeExtension'
            jinja2_extensions = ['zhiyoufy.jinja2.ZhiyoufyExtension']

        self.env = Environment(extensions=jinja2_extensions,
                               loader=self.loader, autoescape=select_autoescape(default_for_string=False))

    def render(self, template_str, context, dst_path=None):
        template_base = self.env.from_string(template_str)
        out = template_base.render(context)

        if dst_path is not None:
            with open(dst_path, "w", encoding="utf-8") as fp:
                fp.write(out)

        return out
