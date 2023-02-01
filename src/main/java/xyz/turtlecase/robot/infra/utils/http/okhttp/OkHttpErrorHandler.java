package xyz.turtlecase.robot.infra.utils.http.okhttp;

/**
 * 异常处理
 */
public interface OkHttpErrorHandler {
    void errorHandler(OkHttpResponse response);
}
