import logging
import threading
import subprocess
import time
import os
import shutil
import psutil

from robot.api import ExecutionResult

from zhiyoufy.common import zhiyoufy_context
from zhiyoufy.common.utils import RandomUtil, IOUtil


def kill(proc_pid):
    process = psutil.Process(proc_pid)
    for proc in process.children(recursive=True):
        proc.kill()
    process.kill()


class JobRunner:
    def __init__(self):
        self.tag = type(self).__name__
        self.log_prefix = "%s:" % self.tag
        self.logger = logging.getLogger(self.tag)

        self.config_inst = zhiyoufy_context.get_config_inst()

        additional_python_path = ""
        if "python_path" in self.config_inst:
            for python_path in self.config_inst.python_path:
                additional_python_path += " --pythonpath %s" % python_path
        self.additional_python_path = additional_python_path

        self.stop_requested = False
        self.poll_condition = threading.Condition()
        self.job_thread = None
        self.end_ok = False
        self.result_ok = False
        self.passed = False
        self.result_guid = None

        self.active_job = None
        self.run_output_dir = None
        self.test_suite_path = None
        self.base_conf_path = None
        self.private_conf_path = None
        self.extra_args = ""
        self.timeout_seconds = None
        self.config_composite = None

        self.finish_callback = None
        self.finish_callback_args = ()

    @property
    def job_key(self):
        return self.active_job.job_key

    def __str__(self):
        return self.job_key

    def stop(self):
        log_prefix = "%s stop:" % self.tag

        self.logger.info("%s Enter stop for %s" % (log_prefix, self.job_key))

        self.stop_requested = True

        with self.poll_condition:
            self.poll_condition.notify()

    def start(self):
        if self.job_thread is None:
            self.job_thread = threading.Thread(
                target=self.run, daemon=True,
                name="%s_%s" % (self.tag, self.job_key))
            self.job_thread.start()

    def run(self):
        log_prefix = f"{self.log_prefix} {self.job_key} run:"

        try:
            IOUtil.maybe_make_dir(self.run_output_dir)

            src_config_dir = os.path.join(self.config_inst.root_dir, os.path.dirname(self.base_conf_path))
            dst_config_dir = os.path.join(self.run_output_dir, os.path.dirname(self.base_conf_path))
            shutil.copytree(src_config_dir, dst_config_dir)

            robotframework_output_dir = os.path.join(self.run_output_dir, "robotframework_output")
            dst_base_conf_path = os.path.join(self.run_output_dir, self.base_conf_path)
            dst_private_config_path = os.path.join(self.run_output_dir, self.private_conf_path)
            abs_test_suite_path = os.path.join(self.config_inst.root_dir, self.test_suite_path)

            private_config_value = ""

            for config_path in self.config_inst.private_config_path:
                private_config_file = os.path.join(self.config_inst.root_dir, config_path)

                with open(private_config_file, "r", encoding="utf-8") as fh:
                    if not private_config_value:
                        private_config_value = "# private config value begin \n"

                    private_config_value += f"# {config_path} \n"
                    private_config_value += fh.read()

            if private_config_value:
                private_config_value += "\n# private config value end \n"

            with open(dst_private_config_path, "w", encoding="utf-8") as fd:
                fd.write("# Start\n")

                fd.write("project_dir=%s\n" % self.run_output_dir.replace("\\", "\\\\"))
                fd.write("rendered_to_output=true\n")
                fd.write("parallel_num=%s\n" % self.active_job["parallelNum"])
                fd.write("child_job_idx=%s\n" % self.active_job["index"])

                fd.write("\n")
                fd.write(self.config_composite)
                fd.write("\n")

                fd.write("# End\n")

                if private_config_value:
                    fd.write("\n")
                    fd.write(private_config_value)
                    fd.write("\n")

            cmd = f"robot --outputdir {robotframework_output_dir} --tagstatinclude stat-*" \
                  f" --variable global_library_config:{dst_base_conf_path} {self.additional_python_path}" \
                  f" {self.extra_args} {abs_test_suite_path}"
            self.logger.info("%s to execute: %s" % (log_prefix, cmd))
            final_time = time.monotonic() + self.timeout_seconds

            proc = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE,
                stderr=subprocess.STDOUT, text=True, encoding="utf-8")

            while True:
                with self.poll_condition:
                    return_code = proc.poll()
                    if return_code is not None:
                        self.end_ok = True
                        break
                    exceed_final_time = True if time.monotonic() > final_time else False
                    if self.stop_requested or exceed_final_time:
                        self.logger.info("%s stop_requested %s, exceed_final_time %s, to call proc kill" % (
                            log_prefix, self.stop_requested, exceed_final_time))
                        kill(proc.pid)
                        break
                    self.poll_condition.wait(3)

                    try:
                        proc.communicate(timeout=0.1)
                    except subprocess.TimeoutExpired:
                        pass

            if self.end_ok:
                self.extract_run_result()

            self.logger.info("%s to call proc communicate" % (log_prefix,))

            proc_stdout, _ = proc.communicate()

            self.logger.info("%s proc_stdout \n %s" % (
                log_prefix, proc_stdout
            ))
        except Exception as e:
            self.logger.error("%s met Exception %s" % (log_prefix, str(e)))
        finally:
            self.result_guid = RandomUtil.gen_guid()
            self.job_thread = None
            if self.finish_callback:
                self.finish_callback(*self.finish_callback_args)
        self.logger.info("%s after execution, end_ok %s, result_ok %s, passed %s, result_guid %s" % (
            log_prefix, self.end_ok, self.result_ok, self.passed, self.result_guid))

    def extract_run_result(self):
        log_prefix = f"{self.log_prefix} {self.job_key} extract_run_result:"

        try:
            robotframework_output_dir = os.path.join(self.run_output_dir, "robotframework_output")
            output_path = os.path.join(robotframework_output_dir, "output.xml")
            result = ExecutionResult(output_path)
            result.configure(stat_config={'suite_stat_level': 4})
            self.passed = result.suite.passed
            self.result_ok = True
        except Exception as e:
            self.logger.error("%s met Exception %s" % (log_prefix, str(e)))

        self.logger.info(f"{log_prefix} result_ok {self.result_ok}, passed {self.passed}")
