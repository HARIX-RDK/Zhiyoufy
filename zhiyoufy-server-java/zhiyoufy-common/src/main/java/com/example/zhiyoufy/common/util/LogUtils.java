package com.example.zhiyoufy.common.util;

import java.util.function.Supplier;

import com.example.zhiyoufy.common.context.ILogContext;
import org.slf4j.Logger;
import org.slf4j.event.Level;

/**
 * log工具类
 */
public class LogUtils {
    public static void log(ILogContext logContext, Level level, Logger logger,
                                 String src, Object arg) {
        switch (level) {
            case TRACE:
                if (logContext.isTraceEnabled() && logger.isTraceEnabled()) {
                    logger.trace("{}: {}: {}", logContext.getTag(), src, arg);
                }
                break;
            case DEBUG:
                if (logContext.isDebugEnabled() && logger.isDebugEnabled()) {
                    logger.debug("{}: {}: {}", logContext.getTag(), src, arg);
                }
                break;
            case INFO:
                if (logContext.isInfoEnabled() && logger.isInfoEnabled()) {
                    logger.info("{}: {}: {}", logContext.getTag(), src, arg);
                }
                break;
            case WARN:
                if (logContext.isWarnEnabled() && logger.isWarnEnabled()) {
                    logger.warn("{}: {}: {}", logContext.getTag(), src, arg);
                }
                break;
            case ERROR:
                if (logContext.isErrorEnabled() && logger.isErrorEnabled()) {
                    logger.error("{}: {}: {}", logContext.getTag(), src, arg);
                }
                break;
        }
    }

    public static void log(ILogContext logContext, Level level, Logger logger,
                           String src, Object arg1, Object arg2) {
        switch (level) {
            case TRACE:
                if (logContext.isTraceEnabled() && logger.isTraceEnabled()) {
                    logger.trace("{}: {}: {} {}", logContext.getTag(), src, arg1, arg2);
                }
                break;
            case DEBUG:
                if (logContext.isDebugEnabled() && logger.isDebugEnabled()) {
                    logger.debug("{}: {}: {} {}", logContext.getTag(), src, arg1, arg2);
                }
                break;
            case INFO:
                if (logContext.isInfoEnabled() && logger.isInfoEnabled()) {
                    logger.info("{}: {}: {} {}", logContext.getTag(), src, arg1, arg2);
                }
                break;
            case WARN:
                if (logContext.isWarnEnabled() && logger.isWarnEnabled()) {
                    logger.warn("{}: {}: {} {}", logContext.getTag(), src, arg1, arg2);
                }
                break;
            case ERROR:
                if (logContext.isErrorEnabled() && logger.isErrorEnabled()) {
                    logger.error("{}: {}: {} {}", logContext.getTag(), src, arg1, arg2);
                }
                break;
        }
    }

    public static void log(ILogContext logContext, Level level, Logger logger,
                                 String src, Supplier<Object> argSupplier) {
        log(logContext, level, logger, src, null, argSupplier);
    }

    @SafeVarargs
    public static void log(ILogContext logContext, Level level, Logger logger,
                           String src, String message, Supplier<Object>... argSuppliers) {
        switch (level) {
            case TRACE:
                if (logContext.isTraceEnabled() && logger.isTraceEnabled()) {
                    if (message != null) {
                        if (argSuppliers.length > 0) {
                            logger.trace("{}: {}: {}", logContext.getTag(), src, format(message, argSuppliers));
                        } else {
                            logger.trace("{}: {}: {}", logContext.getTag(), src, message);
                        }
                    } else if (argSuppliers.length > 0) {
                        logger.trace("{}: {}: {}", logContext.getTag(), src, argSuppliers[0].get());
                    } else {
                        logger.trace("{}: {}", logContext.getTag(), src);
                    }
                }
                break;
            case DEBUG:
                if (logContext.isDebugEnabled() && logger.isDebugEnabled()) {
                    if (message != null) {
                        if (argSuppliers.length > 0) {
                            logger.debug("{}: {}: {}", logContext.getTag(), src, format(message, argSuppliers));
                        } else {
                            logger.debug("{}: {}: {}", logContext.getTag(), src, message);
                        }
                    } else if (argSuppliers.length > 0) {
                        logger.debug("{}: {}: {}", logContext.getTag(), src, argSuppliers[0].get());
                    } else {
                        logger.debug("{}: {}", logContext.getTag(), src);
                    }
                }
                break;
            case INFO:
                if (logContext.isInfoEnabled() && logger.isInfoEnabled()) {
                    if (message != null) {
                        if (argSuppliers.length > 0) {
                            logger.info("{}: {}: {}", logContext.getTag(), src, format(message, argSuppliers));
                        } else {
                            logger.info("{}: {}: {}", logContext.getTag(), src, message);
                        }
                    } else if (argSuppliers.length > 0) {
                        logger.info("{}: {}: {}", logContext.getTag(), src, argSuppliers[0].get());
                    } else {
                        logger.info("{}: {}", logContext.getTag(), src);
                    }
                }
                break;
            case WARN:
                if (logContext.isWarnEnabled() && logger.isWarnEnabled()) {
                    if (message != null) {
                        if (argSuppliers.length > 0) {
                            logger.warn("{}: {}: {}", logContext.getTag(), src, format(message, argSuppliers));
                        } else {
                            logger.warn("{}: {}: {}", logContext.getTag(), src, message);
                        }
                    } else if (argSuppliers.length > 0) {
                        logger.warn("{}: {}: {}", logContext.getTag(), src, argSuppliers[0].get());
                    } else {
                        logger.warn("{}: {}", logContext.getTag(), src);
                    }
                }
                break;
            case ERROR:
                if (logContext.isErrorEnabled() && logger.isErrorEnabled()) {
                    if (message != null) {
                        if (argSuppliers.length > 0) {
                            logger.error("{}: {}: {}", logContext.getTag(), src, format(message, argSuppliers));
                        } else {
                            logger.error("{}: {}: {}", logContext.getTag(), src, message);
                        }
                    } else if (argSuppliers.length > 0) {
                        logger.error("{}: {}: {}", logContext.getTag(), src, argSuppliers[0].get());
                    } else {
                        logger.error("{}: {}", logContext.getTag(), src);
                    }
                }
                break;
        }
    }

    @SafeVarargs
    public static void log(Level level, Logger logger, String message, Supplier<Object>... argSuppliers) {
        switch (level) {
            case TRACE:
                if (logger.isTraceEnabled()) {
                    logger.trace(format(message, argSuppliers));
                }
                break;
            case DEBUG:
                if (logger.isDebugEnabled()) {
                    logger.debug(format(message, argSuppliers));
                }
                break;
            case INFO:
                if (logger.isInfoEnabled()) {
                    logger.info(format(message, argSuppliers));
                }
                break;
            case WARN:
                if (logger.isWarnEnabled()) {
                    logger.warn(format(message, argSuppliers));
                }
                break;
            case ERROR:
                if (logger.isErrorEnabled()) {
                    logger.error(format(message, argSuppliers));
                }
                break;
        }
    }

    @SafeVarargs
    private static String format(String message, Supplier<Object>... argSuppliers) {
        Object[] args = new Object[argSuppliers.length];
        int i = 0;
        for (Supplier<Object> a : argSuppliers) {
            args[i] = a.get();
            ++i;
        }

        return String.format(message, args);
    }
}
