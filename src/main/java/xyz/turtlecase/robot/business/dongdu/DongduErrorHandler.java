package xyz.turtlecase.robot.business.dongdu;

import xyz.turtlecase.robot.infra.exception.BaseException;
import xyz.turtlecase.robot.infra.utils.http.okhttp.OkHttpErrorHandler;
import xyz.turtlecase.robot.infra.utils.http.okhttp.OkHttpResponse;

public class DongduErrorHandler implements OkHttpErrorHandler {
    /**
     * 异常处理
     *
     * @param response
     */
    public void errorHandler(OkHttpResponse response) {
        if (!Boolean.TRUE.equals(response.getSuccess())) {
            throw new BaseException(response.getBody());
        }
    }
}
