import json
import os
import logging
import random
import time
import zipfile
from datetime import datetime, timedelta
import shutil

import requests

from zhiyoufy.common import zhiyoufy_context
from zhiyoufy.common.utils import LogUtil, TimeUtil, RandomUtil


class IOUtil:
    @staticmethod
    def download_file(url, local_filepath, ssl_verify=False, max_retry=2):
        for i in range(max_retry):
            try:
                with requests.get(url, stream=True, verify=ssl_verify) as r:
                    r.raise_for_status()
                    with open(local_filepath, 'wb') as f:
                        for chunk in r.iter_content(chunk_size=8192):
                            if chunk:  # filter out keep-alive new chunks
                                f.write(chunk)
                return
            except Exception as e:
                if i == (max_retry - 1):
                    LogUtil.log_then_rethrown("download_file %s:" % url, e)

    @staticmethod
    def download_if_needed(src_url, target_dir, timeout):
        base_name = src_url.rsplit('/', 1)[1]
        target_path = os.path.join(target_dir, base_name)

        if os.path.exists(target_path):
            return target_path

        target_path_flag = target_path + ".flag"
        flag = {
            "create_time": TimeUtil.get_current_time_isoformat(),
            "run_id": RandomUtil.gen_guid()
        }
        target_path_temp = target_path + ".temp"

        winner_run_id = None
        final_end_time = time.monotonic() + timeout
        while time.monotonic() < final_end_time:
            if winner_run_id is None:
                if os.path.exists(target_path_flag):
                    for read_idx in range(2):
                        try:
                            with open(target_path_flag, 'r', encoding="utf-8") as fh:
                                existing_flag = json.load(fh)
                                winner_create_time = TimeUtil.datetime_fromisoformat(existing_flag["create_time"])
                                winner_run_id = existing_flag["run_id"]
                                break
                        except Exception as e:
                            time.sleep(0.1 + random.random())

            if winner_run_id is None:
                try:
                    with open(target_path_flag, 'w', encoding="utf-8") as fh:
                        fh.write(json.dumps(flag, indent=2))
                except Exception:
                    time.sleep(0.1 + random.random())
                continue

            if winner_run_id == flag["run_id"]:
                try:
                    zhiyoufy_context.get_robot_logger().info("downloading %s: winner_run_id %s" % (
                        src_url, winner_run_id))

                    r = requests.get(src_url, stream=True, verify=False)
                    total_size_in_bytes = int(r.headers.get('content-length', 0))
                    block_size = 8096
                    total_written = 0

                    with open(target_path_temp, 'wb') as fh:
                        for data in r.iter_content(block_size):
                            total_written += len(data)
                            fh.write(data)
                            zhiyoufy_context.get_robot_logger().info("downloading %s: written %s / %s" % (
                                src_url, total_written, total_size_in_bytes))
                    os.remove(target_path_flag)
                    os.rename(target_path_temp, target_path)
                    return target_path
                except Exception as e:
                    zhiyoufy_context.get_robot_logger().info("downloading %s: exception %s" % (
                        src_url, e))
                    time.sleep(0.1)
            else:
                if abs((winner_create_time.utcnow() - datetime.utcnow()).total_seconds()) > (timeout + 60):
                    try:
                        os.remove(target_path_flag)
                    except Exception:
                        time.sleep(0.1)
                    winner_run_id = None

        raise Exception("download %s timeout" % src_url)

    @staticmethod
    def download_url(url, retry_count=3, timeout=5, rethrow=True):
        for idx in range(retry_count):
            zhiyoufy_context.get_global_context().throttle.wait(url)
            try:
                zhiyoufy_context.get_robot_logger().debug(f"Try {idx} Begin: to download from: {url}")
                rsp = requests.get(url, timeout=timeout)
                zhiyoufy_context.get_robot_logger().debug(f"Try {idx} End: finish download from: {url}, rsp.status_code {rsp.status_code}")

                if not 200 <= rsp.status_code < 300:
                    zhiyoufy_context.get_robot_logger().warning(f"Try {idx} failed: url {url}, rsp code {rsp.status_code}, text {rsp.text}")
                    continue

                return rsp
            except Exception as e:
                zhiyoufy_context.get_robot_logger().info("warn: download %s failed: %s" % (url, str(e)))
                time.sleep(10)

                if idx == (retry_count - 1):
                    zhiyoufy_context.get_robot_logger().warning(f"error: download {url} failed")

                    if rethrow:
                        raise e

        raise Exception(f"Failed to download {url}")
    
    @staticmethod
    def find_abs_path(in_path, iter_lvl=5):
        log_prefix = "find_file:"

        logging.info("%s Enter to find %s" % (log_prefix, in_path))

        found = False

        if os.path.isabs(in_path):
            if os.path.exists(in_path):
                abs_path = in_path
                found = True
        else:
            rel_path = in_path
            for i in range(iter_lvl):
                if os.path.exists(rel_path):
                    abs_path = os.path.abspath(rel_path)
                    found = True
                    break
                rel_path = os.path.join("..", rel_path)

        if not found:
            logging.info("%s Leave without found" % (log_prefix,))
            return None

        logging.info("%s Leave with found %s" % (log_prefix, abs_path))

        return abs_path

    @staticmethod
    def find_root_dir(config_file, check_dir_names=None):
        prev_dir = None
        cur_dir = os.path.dirname(config_file)

        if not check_dir_names:
            check_dir_names = ["data", "config"]

        while True:
            if cur_dir == prev_dir:
                raise Exception("Can't find root dir for %s" % config_file)

            all_check_pass = True

            for check_dir_name in check_dir_names:
                check_dir = os.path.join(cur_dir, check_dir_name)

                if not os.path.isdir(check_dir):
                    all_check_pass = False
                    break

            if all_check_pass:
                return cur_dir

            prev_dir = cur_dir
            cur_dir = os.path.abspath(os.path.join(cur_dir, ".."))

    @staticmethod
    def get_local_abs_file_path(file_path, timeout):
        # download from remote if not exist in local and cache in local
        if file_path.startswith("http"):
            local_abs_file_path = IOUtil.download_if_needed(
                file_path, zhiyoufy_context.get_config_inst().data_cache_dir, timeout)
        else:
            local_abs_file_path = os.path.join(zhiyoufy_context.get_config_inst().data_dir, file_path)
            if not os.path.exists(local_abs_file_path):
                raise Exception("get_local_abs_file_path failed, file_path %s doesn't exist" % (
                    local_abs_file_path,))

        return local_abs_file_path

    @staticmethod
    def load_json_from_url(json_url, timeout=10):
        text = IOUtil.load_text_from_url(json_url, timeout=timeout)
        json_data = json.loads(text)
        return json_data

    @staticmethod
    def load_text_from_url(text_url, timeout=10):
        if text_url.startswith("http"):
            return IOUtil.load_text_from_http_url(text_url, timeout)
        else:
            return IOUtil.load_text_from_file_url(text_url)

    @staticmethod
    def load_text_from_http_url(text_url, timeout):
        headers = {}

        r = requests.get(text_url, headers=headers,
                         timeout=timeout, verify=False)

        if 200 <= r.status_code < 300:
            text_content = r.text
            return text_content
        else:
            raise Exception("bad status code %s" % r.status_code)

    @staticmethod
    def load_text_from_file_url(text_url):
        with open(text_url, "r", encoding="utf-8") as text_file:
            text_content = text_file.read()
            return text_content

    @staticmethod
    def maybe_make_dir(dirname):
        """Make a directory if it doesn't exist."""
        os.makedirs(dirname, exist_ok=True)

    @staticmethod
    def rm_file(file_path):
        log_prefix = "rm_file:"

        try:
            os.remove(file_path)
            logging.info(f"{log_prefix} file_path: {file_path}")
        except Exception as err:
            logging.info(f"{log_prefix} exception {err}")

    @staticmethod
    def rm_old(dir_path, days=7, force=False, filter_list=None, remove_indeed=True):
        log_prefix = "rm_old:"

        if not os.path.isdir(dir_path):
            return

        datetime_thres_obj = datetime.now() - timedelta(days=days)
        datetime_thres = datetime_thres_obj.timestamp()
        logging.info("%s dir_path: %s, force_delete: %s, remove_indeed %s, datetime_thres %s"
                     % (log_prefix, dir_path, force, remove_indeed, datetime_thres_obj))

        with os.scandir(dir_path) as it:
            for entry in it:
                stat_result = entry.stat()
                if False:
                    logging.info("%s, path: %s, st_mtime: %s, date_time_thres: %s, cur_time: %s"
                                 % (log_prefix, entry.path, stat_result.st_mtime, datetime_thres, datetime.now()))
                # 1604799194 is 2020-11-08 09:33:14
                if force or datetime_thres > stat_result.st_mtime > 1604799194:
                    if entry.is_dir():
                        logging.info("%s, rm dir %s" % (log_prefix, entry.path))
                        if remove_indeed:
                            if not filter_list or (filter_list and entry.path not in filter_list):
                                shutil.rmtree(entry.path)
                                logging.info("%s, rm dir %s done" % (log_prefix, entry.path))
                    else:
                        logging.info("%s, rm file %s" % (log_prefix, entry.path))
                        if remove_indeed:
                            if not filter_list or (filter_list and entry.path not in filter_list):
                                os.remove(entry.path)
                                logging.info("%s, rm file %s done" % (log_prefix, entry.path))

    @staticmethod
    def save_text_to_path(in_text, out_path):
        with open(out_path, "w", encoding="utf-8") as fh:
            fh.write(in_text)

    @staticmethod
    def unzip_to_dir(zip_path, dir_path=None):
        with zipfile.ZipFile(zip_path, 'r') as zf:
            zf.extractall(dir_path)

    @staticmethod
    def zip_path(in_path, zip_file_path=None):
        def addToZip(zf, path, zippath):
            if os.path.isfile(path):
                zf.write(path, zippath, zipfile.ZIP_DEFLATED)
            elif os.path.isdir(path):
                if zippath:
                    zf.write(path, zippath)
                for nm in sorted(os.listdir(path)):
                    addToZip(zf,
                             os.path.join(path, nm), os.path.join(zippath, nm))

        if zip_file_path is None:
            zip_file_path = os.path.basename(in_path) + ".zip"
        with zipfile.ZipFile(zip_file_path, 'w') as zf:
            zippath = os.path.basename(in_path)
            if not zippath:
                zippath = os.path.basename(os.path.dirname(in_path))
            if zippath in ('', os.curdir, os.pardir):
                zippath = ''
            addToZip(zf, in_path, zippath)
