package xyz.turtlecase.robot.infra.web;

import xyz.turtlecase.robot.infra.exception.BaseException;

/**
 * 便捷的REST返回结果封装生成器
 */
public class RestResultGenerator {
    public static <T> RestResult<T> genResult(boolean success, Integer code, String message, T data) {
        return new RestResult(success, code, message, data);
    }

    /**
     * 生成成功的REST结果
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> RestResult<T> genSuccessResult(T data) {
        return genResult(true, RestResult.DEFAULT_SUCCESS_CODE, "success", data);
    }

    public static <T> RestResult<T> genSuccessResult(String message, T data) {
        return genResult(true, RestResult.DEFAULT_SUCCESS_CODE, message, data);
    }

    /**
     * 无实体的成功调用
     *
     * @return
     */
    public static RestResult genSuccessResult() {
        return genSuccessResult(null);
    }

    /**
     * 返回异常的调用
     *
     * @param message
     * @param data
     * @param <T>
     * @return
     */
    public static <T> RestResult<T> genErrorResult(String message, T data) {
        return genResult(false, RestResult.DEFAULT_ERROR_CODE, message, data);
    }

    public static <T> RestResult<T> genErrorResult(String message) {
        return genErrorResult(message, null);
    }

    /**
     * 带异常的失败调用
     *
     * @param error
     * @param <T>
     * @return
     */
    public static <T> RestResult<T> genErrorResult(BaseException error) {
        return genErrorResult(error.getMessage());
    }

    /**
     * 无数据的失败调用
     *
     * @return
     */
    public static RestResult genErrorResult() {
        return genErrorResult("error");
    }
}
