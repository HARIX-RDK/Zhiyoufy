package com.example.zhiyoufy.common.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 基本响应格式，reqId用于标识请求，error只当遇到错误时才设置
 * traceRecords用于按需绑定处理过程中log，方便调试
 */
@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResult<T> {
    private String reqId;
    private ErrorInfo error;
    private List<String> traceRecords;
    private String detailMsg;
    private Long costTimeMs;

    /**
     * 数据封装
     */
    private T data;

    protected CommonResult() {
    }

    protected CommonResult(String reqId, ErrorInfo errorInfo) {
        this.reqId = reqId;
        this.error = errorInfo;
    }

    protected CommonResult(ErrorInfo errorInfo) {
        this.error = errorInfo;
    }

    protected CommonResult(T data) {
        this.data = data;
    }

    public static CommonResult success() {
        return new CommonResult();
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<T>(data);
    }

    /**
     * 失败返回结果
     * @param errorCode 错误码
     */
    public static <T> CommonResult<T> failed(IErrorCode errorCode) {
        return new CommonResult<T>(ErrorInfo.of(errorCode));
    }

    /**
     * 失败返回结果
     */
    public static <T> CommonResult<T> failed(IErrorCode errorCode, String detail) {
        return new CommonResult<T>(ErrorInfo.of(errorCode, detail));
    }

    /**
     * 失败返回结果
     */
    public static <T> CommonResult<T> failed() {
        return failed(CommonErrorCode.RES_FAILED);
    }

    /**
     * 参数验证失败返回结果
     */
    public static <T> CommonResult<T> validateFailed() {
        return failed(CommonErrorCode.RES_VALIDATE_FAILED);
    }

    /**
     * 参数验证失败返回结果
     */
    public static <T> CommonResult<T> validateFailed(String detail) {
        return failed(CommonErrorCode.RES_VALIDATE_FAILED, detail);
    }

    /**
     * 未登录返回结果
     */
    public static <T> CommonResult<T> unauthorized() {
        return failed(CommonErrorCode.RES_UNAUTHORIZED);
    }

    /**
     * 未授权返回结果
     */
    public static <T> CommonResult<T> forbidden() {
        return failed(CommonErrorCode.RES_FORBIDDEN);
    }

    public static <T> CommonResult<T> forbidden(String detail) {
        return failed(CommonErrorCode.RES_FORBIDDEN, detail);
    }
}
