package xyz.turtlecase.robot.infra.utils.http.okhttp;

import lombok.Builder;
import lombok.Data;

/**
 * 响应体
 */
@Data
@Builder
public class OkHttpResponse {
    /**
     * 请示是否成功
     */
    private Boolean success;
    /**
     * http状态码
     */
    private Integer httpStatus;
    /**
     * 返回内容
     */
    private String body;
}
