package com.example.zhiyoufy.common.api;

/**
 * 定义接口从而各模块可以分别定义满足规范的错误码
 */
public interface IErrorCode {
    /**
     * 返回码
     */
    int getCode();

    /**
     * 返回信息
     */
    String getMessage();

    String getDetail();
}
