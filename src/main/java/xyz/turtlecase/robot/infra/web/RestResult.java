package xyz.turtlecase.robot.infra.web;

import java.io.Serializable;

/**
 * REST返回的结果封装
 *
 * @param <T>
 */
public final class RestResult<T> implements Serializable {
    public static final String DEFAULT_SUCCESS_MESSAGE = "success";
    public static final String DEFAULT_ERROR_MESSAGE = "error";
    public static final Integer DEFAULT_SUCCESS_CODE = 200;
    public static final Integer DEFAULT_ERROR_CODE = 500;
    /**
     * 调用是否成功
     */
    private boolean success;
    /**
     * 与http状态码一致, 可以合适spring的HttpStatus
     *
     * @see org.springframework.http.HttpStatus
     */
    private Integer code;
    /**
     * 异常提示消息
     */
    private String message;
    /**
     * 数据
     */
    private T data;

    public RestResult(boolean success, Integer code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
