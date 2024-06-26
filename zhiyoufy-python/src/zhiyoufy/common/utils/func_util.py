import time
import traceback
from datetime import datetime, timedelta
from functools import wraps

from zhiyoufy.common import zhiyoufy_context


class FuncUtil:
    @staticmethod
    def retry(ExceptionToCheck, tries=4, delay=3, backoff=2, logger=None):
        """Retry calling the decorated function using an exponential backoff.

        http://www.saltycrane.com/blog/2009/11/trying-out-retry-decorator-python/
        original from: http://wiki.python.org/moin/PythonDecoratorLibrary#Retry

        :param ExceptionToCheck: the exception to check. may be a tuple of
            exceptions to check
        :type ExceptionToCheck: Exception or tuple
        :param tries: number of times to try (not retry) before giving up
        :type tries: int
        :param delay: initial delay between retries in seconds
        :type delay: int
        :param backoff: backoff multiplier e.g. value of 2 will double the delay
            each retry
        :type backoff: int
        :param logger: logger to use. If None, print
        :type logger: logging.Logger instance
        """

        def deco_retry(f):

            @wraps(f)
            def f_retry(*args, **kwargs):
                mtries, mdelay = tries, delay
                while mtries > 1:
                    try:
                        return f(*args, **kwargs)
                    except ExceptionToCheck as e:
                        zhiyoufy_context.get_robot_logger().info(traceback.format_exc())
                        msg = "%s, Retrying in %d seconds..." % (str(e), mdelay)
                        if logger:
                            logger.warning(msg)
                        else:
                            zhiyoufy_context.get_robot_logger().warning(msg)
                        time.sleep(mdelay)
                        mtries -= 1
                        mdelay *= backoff
                return f(*args, **kwargs)

            return f_retry  # true decorator

        return deco_retry

    @staticmethod
    def retry_until_timeout(func, timeout, sleep_time=1):
        final_time = datetime.utcnow() + timedelta(seconds=timeout)

        first = True
        while datetime.utcnow() <= final_time:
            if first:
                rsp = func()
                first = False
            else:
                time.sleep(sleep_time)
                rsp = func()

            if rsp is None:
                continue
            else:
                return rsp

        return None

    @staticmethod
    def run_ignore_exception(func):
        try:
            func()
        except Exception:
            pass
