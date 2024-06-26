package com.example.zhiyoufy.common.context;

import org.slf4j.event.Level;

/**
 * log上下文接口定义
 */
public interface ILogContext {
    String getTag();
    Level getLogLevelThreshold();

    default boolean isTraceEnabled() {
        return getLogLevelThreshold().toInt() <= Level.TRACE.toInt();
    }

    default boolean isDebugEnabled() {
        return getLogLevelThreshold().toInt() <= Level.DEBUG.toInt();
    }

    default boolean isInfoEnabled() {
        return getLogLevelThreshold().toInt() <= Level.INFO.toInt();
    }

    default boolean isWarnEnabled() {
        return getLogLevelThreshold().toInt() <= Level.WARN.toInt();
    }

    default boolean isErrorEnabled() {
        return getLogLevelThreshold().toInt() <= Level.ERROR.toInt();
    }
}
