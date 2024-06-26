import concurrent.futures
import time
import traceback
from collections import namedtuple

import pandas as pd

from .str_util import StrUtil

MetricData = namedtuple('MetricData', ['start_time', 'finish_time', 'cost_time'])


class TraceInsideException(Exception):
    def __init__(self, traces):
        self.traces = traces


class PerfUtil:
    def __init__(self, config):
        self.max_workers = config["max_workers"]
        self.min_interval = config["min_interval"]
        self.duration = config["duration"]
        self.logger = config["logger"]
        self.expected_metric_data = config["expected_metric_data"]

        self.last_1_minute_metrics = []
        self.last_1_minute_metrics_cost_time = []
        self.last_1_minute_cost_time_sum = 0.0

        self.last_5_minutes_metrics = []
        self.last_5_minutes_metrics_cost_time = []
        self.last_5_minutes_cost_time_sum = 0.0

        self.total_call_cnt = 0
        self.total_cost_time_sum = 0.0
        self.last_dump_time = 0.0

        # adding the error call metrics
        self.last_1_minute_error_metrics = []
        self.last_1_minute_error_cnt = 0

        self.last_5_minutes_error_metrics = []
        self.last_5_minutes_error_cnt = 0

        self.total_error_call_cnt = 0

    def dump_metrics(self):
        last_1_minute_metrics = pd.Series(self.last_1_minute_metrics_cost_time).describe()
        self.logger.info(
            "last_1_minute_metrics pandas describe \n%s" % last_1_minute_metrics)

        self.check_metric_data(last_1_minute_metrics)

        last_5_minutes_metrics = pd.Series(self.last_5_minutes_metrics_cost_time).describe()
        self.logger.info(
            "last_5_minutes_metrics pandas describe \n%s" % last_5_minutes_metrics)

        self.check_metric_data(last_5_minutes_metrics)

        if self.total_call_cnt:
            total_cost_time_avg = self.total_cost_time_sum / self.total_call_cnt
        else:
            total_cost_time_avg = 0
        self.logger.info("total_call_cnt %d, total_cost_time_avg %.3f",
                         self.total_call_cnt, total_cost_time_avg)

        self.last_dump_time = time.monotonic()

    def check_metric_data(self, metric_data):
        for key,value in self.expected_metric_data.items():
            if metric_data.get(key) > value:
                raise Exception("metric data not meet the target!")

    def process_new_metric_data(self, metric_data):
        cur_time = time.monotonic()
        time_1_minute_ago = cur_time - 60
        time_5_minutes_ago = cur_time - 300

        while len(self.last_1_minute_metrics):
            if self.last_1_minute_metrics[0].finish_time >= time_1_minute_ago:
                break

            self.last_1_minute_cost_time_sum -= self.last_1_minute_metrics[0].cost_time

            del self.last_1_minute_metrics[0]
            del self.last_1_minute_metrics_cost_time[0]

        while len(self.last_5_minutes_metrics):
            if self.last_5_minutes_metrics[0].finish_time >= time_5_minutes_ago:
                break

            self.last_5_minutes_cost_time_sum -= self.last_5_minutes_metrics[0].cost_time

            del self.last_5_minutes_metrics[0]
            del self.last_5_minutes_metrics_cost_time[0]

        self.last_1_minute_metrics.append(metric_data)
        self.last_1_minute_metrics_cost_time.append(metric_data.cost_time)
        self.last_1_minute_cost_time_sum += metric_data.cost_time

        self.last_5_minutes_metrics.append(metric_data)
        self.last_5_minutes_metrics_cost_time.append(metric_data.cost_time)
        self.last_5_minutes_cost_time_sum += metric_data.cost_time

        self.total_call_cnt += 1
        self.total_cost_time_sum += metric_data.cost_time

    def perf_test(self, func, func_result=None):
        final_end_time = time.monotonic() + self.duration

        call_idx = 0
        start_time = time.monotonic()
        func(call_idx, True)

        cur_time = time.monotonic()

        cost_time = 0

        if func_result:
            cost_time = func_result(call_idx)

        if cost_time <= 0:
            cost_time = cur_time - start_time
            cost_time = cost_time * 1000

        metric_data = MetricData(start_time=start_time, finish_time=cur_time, cost_time=cost_time)
        self.process_new_metric_data(metric_data)
        self.dump_metrics()

        call_idx += 1

        with concurrent.futures.ThreadPoolExecutor(max_workers=self.max_workers) as executor:
            future_to_call_idx = {}
            call_idx_to_start_time = {}
            for i in range(self.max_workers):
                call_idx_to_start_time[call_idx] = time.monotonic()
                future = executor.submit(func, call_idx)
                future_to_call_idx[future] = call_idx
                call_idx += 1
            last_submit_time = time.monotonic()
            while True:
                for future in concurrent.futures.as_completed(future_to_call_idx):
                    func_call_idx = future_to_call_idx[future]
                    try:
                        future.result()
                        break
                    except TraceInsideException as exc:
                        self.logger.info("embedded traces: %s" % StrUtil.pprint(exc.traces))
                        self.logger.error(traceback.format_exc())
                        raise exc.__cause__
                    except Exception as exc:
                        self.logger.error('%s generated an exception: %s' % (func_call_idx, exc))
                        raise exc

                del future_to_call_idx[future]
                start_time = call_idx_to_start_time.pop(func_call_idx)
                finish_time = time.monotonic()
                cost_time = 0

                if func_result:
                    cost_time = func_result(func_call_idx)

                if cost_time <= 0:
                    cost_time = cur_time - start_time
                    cost_time = cost_time * 1000

                metric_data = MetricData(start_time, finish_time, cost_time)
                self.process_new_metric_data(metric_data)

                if finish_time > (self.last_dump_time + 20):
                    self.dump_metrics()

                if finish_time > final_end_time:
                    break

                if self.min_interval:
                    cur_time = time.monotonic()
                    if cur_time < (last_submit_time + self.min_interval):
                        time.sleep((last_submit_time + self.min_interval) - cur_time)

                call_idx_to_start_time[call_idx] = time.monotonic()
                future = executor.submit(func, call_idx)
                future_to_call_idx[future] = call_idx
                call_idx += 1
                last_submit_time = time.monotonic()

            for future in concurrent.futures.as_completed(future_to_call_idx):
                func_call_idx = future_to_call_idx[future]
                try:
                    future.result()

                    start_time = call_idx_to_start_time.pop(func_call_idx)
                    finish_time = time.monotonic()
                    cost_time = 0

                    if func_result:
                        cost_time = func_result(func_call_idx)

                    if cost_time <= 0:
                        cost_time = cur_time - start_time
                        cost_time = cost_time * 1000

                    metric_data = MetricData(start_time, finish_time, cost_time)
                    self.process_new_metric_data(metric_data)
                except TraceInsideException as exc:
                    self.logger.info("embedded traces: %s" % StrUtil.pprint(exc.traces))
                    self.logger.error(traceback.format_exc())
                    raise exc.__cause__
                except Exception as exc:
                    self.logger.error('%s generated an exception: %s' % (func_call_idx, exc))
                    raise exc

        start_time = time.monotonic()
        func(call_idx, True)

        cur_time = time.monotonic()
        cost_time = 0

        if func_result:
            cost_time = func_result(call_idx)

        if cost_time <= 0:
            cost_time = cur_time - start_time
            cost_time = cost_time * 1000

        metric_data = MetricData(start_time=start_time, finish_time=cur_time, cost_time=cost_time)
        call_idx += 1
        self.process_new_metric_data(metric_data)

        self.dump_metrics()

    def dump_error_metrics(self):
        self.logger.info(
            "last_1_minute_error_metrics: error_count: %d\n" % self.last_1_minute_error_cnt)

        self.logger.info(
            "last_5_minutes_error_metrics: error_count: %d\n" % self.last_5_minutes_error_cnt)

        self.logger.info("total_error_call_cnt %d\n", self.total_error_call_cnt)

        self.last_dump_time = time.monotonic()

    def process_new_error_metric_data(self, metric_data):
        cur_time = time.monotonic()
        time_1_minute_ago = cur_time - 60
        time_5_minutes_ago = cur_time - 300

        while len(self.last_1_minute_error_metrics):
            if self.last_1_minute_error_metrics[0].finish_time >= time_1_minute_ago:
                break

            del self.last_1_minute_error_metrics[0]

        while len(self.last_5_minutes_error_metrics):
            if self.last_5_minutes_error_metrics[0].finish_time >= time_5_minutes_ago:
                break

            del self.last_5_minutes_error_metrics[0]

        self.last_1_minute_error_metrics.append(metric_data)

        self.last_5_minutes_error_metrics.append(metric_data)

        self.last_1_minute_error_cnt = len(self.last_1_minute_error_metrics)
        self.last_5_minutes_error_cnt = len(self.last_5_minutes_error_metrics)
        self.total_error_call_cnt += 1

    def perf_test_no_exit_on_error(self, func):
        final_end_time = time.monotonic() + self.duration
        call_idx = 0
        start_time = time.monotonic()
        try:
            func(call_idx, True)
            call_idx += 1
            cur_time = time.monotonic()
            metric_data = MetricData(start_time=start_time, finish_time=cur_time, cost_time=cur_time - start_time)
            self.process_new_metric_data(metric_data)
        except TraceInsideException as exc:
            #self.logger.info("embedded traces: %s" % StrUtil.pprint(exc.traces))
            #self.logger.error(traceback.format_exc())
            call_idx += 1
            cur_time = time.monotonic()
            metric_data = MetricData(start_time=start_time, finish_time=cur_time, cost_time=cur_time - start_time)
            self.process_new_error_metric_data(metric_data)
        except Exception as exc:
            self.logger.error('%s generated an exception: %s' % (call_idx, exc))
            call_idx += 1
            cur_time = time.monotonic()
            metric_data = MetricData(start_time=start_time, finish_time=cur_time, cost_time=cur_time - start_time)
            self.process_new_error_metric_data(metric_data)
        finally:
            self.dump_metrics()
            self.dump_error_metrics()

        with concurrent.futures.ThreadPoolExecutor(max_workers=self.max_workers) as executor:
            future_to_call_idx = {}
            call_idx_to_start_time = {}
            for i in range(self.max_workers):
                call_idx_to_start_time[call_idx] = time.monotonic()
                future = executor.submit(func, call_idx)
                future_to_call_idx[future] = call_idx
                call_idx += 1
            last_submit_time = time.monotonic()
            while True:
                for future in concurrent.futures.as_completed(future_to_call_idx):
                    func_call_idx = future_to_call_idx[future]
                    result = True
                    try:
                        future.result()
                        break
                    except TraceInsideException as exc:
                        #self.logger.info("embedded traces: %s" % StrUtil.pprint(exc.traces))
                        #self.logger.error(traceback.format_exc())
                        result = False
                        break
                    except Exception as exc:
                        self.logger.error('%s generated an exception: %s' % (func_call_idx, exc))
                        result = False
                        break

                del future_to_call_idx[future]
                start_time = call_idx_to_start_time.pop(func_call_idx)
                finish_time = time.monotonic()
                metric_data = MetricData(start_time, finish_time, finish_time - start_time)
                if result:
                    self.process_new_metric_data(metric_data)
                else:
                    self.process_new_error_metric_data(metric_data)

                if finish_time > (self.last_dump_time + 20):
                    self.dump_metrics()
                    self.dump_error_metrics()

                if finish_time > final_end_time:
                    break

                if self.min_interval:
                    cur_time = time.monotonic()
                    if cur_time < (last_submit_time + self.min_interval):
                        time.sleep((last_submit_time + self.min_interval) - cur_time)

                call_idx_to_start_time[call_idx] = time.monotonic()
                future = executor.submit(func, call_idx)
                future_to_call_idx[future] = call_idx
                call_idx += 1
                last_submit_time = time.monotonic()

            for future in concurrent.futures.as_completed(future_to_call_idx):
                func_call_idx = future_to_call_idx[future]
                try:
                    future.result()

                    start_time = call_idx_to_start_time.pop(func_call_idx)
                    finish_time = time.monotonic()
                    metric_data = MetricData(start_time, finish_time, finish_time - start_time)
                    self.process_new_metric_data(metric_data)
                except TraceInsideException as exc:
                    #self.logger.info("embedded traces: %s" % StrUtil.pprint(exc.traces))
                    #self.logger.error(traceback.format_exc())
                    start_time = call_idx_to_start_time.pop(func_call_idx)
                    finish_time = time.monotonic()
                    metric_data = MetricData(start_time, finish_time, finish_time - start_time)
                    self.process_new_error_metric_data(metric_data)
                except Exception as exc:
                    self.logger.error('%s generated an exception: %s' % (func_call_idx, exc))
                    start_time = call_idx_to_start_time.pop(func_call_idx)
                    finish_time = time.monotonic()
                    metric_data = MetricData(start_time, finish_time, finish_time - start_time)
                    self.process_new_error_metric_data(metric_data)

        start_time = time.monotonic()
        try:
            func(call_idx, True)
            call_idx += 1
            cur_time = time.monotonic()
            metric_data = MetricData(start_time=start_time, finish_time=cur_time, cost_time=cur_time - start_time)
            self.process_new_metric_data(metric_data)
        except TraceInsideException as exc:
            #self.logger.info("embedded traces: %s" % StrUtil.pprint(exc.traces))
            #self.logger.error(traceback.format_exc())
            call_idx += 1
            cur_time = time.monotonic()
            metric_data = MetricData(start_time=start_time, finish_time=cur_time, cost_time=cur_time - start_time)
            self.process_new_error_metric_data(metric_data)
        except Exception as exc:
            self.logger.error('%s generated an exception: %s' % (call_idx, exc))
            call_idx += 1
            cur_time = time.monotonic()
            metric_data = MetricData(start_time=start_time, finish_time=cur_time, cost_time=cur_time - start_time)
            self.process_new_error_metric_data(metric_data)

        finally:
            self.dump_metrics()
            self.dump_error_metrics()
            if self.total_error_call_cnt != 0:
                raise Exception("error count is not zero")
