import argparse
import logging
import os
import subprocess
import sys
import time
from datetime import datetime

from zhiyoufy.common import init_app_base, zhiyoufy_context
from zhiyoufy.common.utils import IOUtil


def handle_arguments(cl_arguments):
    parser = argparse.ArgumentParser(description='')
    # Configuration files
    parser.add_argument('--config_file', '-c', type=str,
                        help="Config file(s) (.conf) for model parameters.")

    return parser.parse_args(cl_arguments)


def main(cl_arguments):
    ''' Train or load a model. Evaluate on some tasks. '''
    cl_args = handle_arguments(cl_arguments)

    init_app_base.init_app_base(cl_args.config_file)

    config_inst = zhiyoufy_context.get_config_inst()

    begin_time = datetime.now().strftime("%Y%m%d_%H%M%S")
    run_output_dir = os.path.join(config_inst.output_dir, begin_time)
    IOUtil.maybe_make_dir(run_output_dir)

    IOUtil.rm_old(config_inst.output_dir)

    # Prepare data #
    logging.info("Start working...")

    additional_python_path = ""
    if "python_path" in config_inst:
        for python_path in config_inst.python_path:
            additional_python_path += " --pythonpath %s" % python_path

    test_include = ""
    if "tags_include" in config_inst:
        for tag_include in config_inst.tags_include:
            test_include += " --include %s" % tag_include

    test_exclude = ""
    if "tags_exclude" in config_inst:
        for tag_exclude in config_inst.tags_exclude:
            test_exclude += " --exclude %s" % tag_exclude

    cmd = "robot --outputdir %s --tagstatexclude nostat-* --variable global_library_config:%s %s %s %s %s" % \
          (run_output_dir, config_inst.global_library_config, additional_python_path,
           test_include, test_exclude, config_inst.test_target)

    logging.info("to execute: %s" % cmd)
    start_time = time.time()

    subprocess.run(cmd, shell=True)
    logging.info("after execution")

    end_time = time.time()
    logging.info('Finished working in %.3fs', end_time - start_time)


if __name__ == '__main__':
    main(sys.argv[1:])
