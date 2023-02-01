package xyz.turtlecase.robot.business.task.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 回调webApp api数据
 */
@Data
@EqualsAndHashCode
public class CallbackData {
    /**
     * callback类型
     */
    private String type;
    /**
     * 数据
     */
    private Map<String, Object> data;
    /**
     * 最大尝试次数
     */
    private Integer maxRetryTimes = 10;
    /**
     * 当前尝试次数
     */
    private Integer retryTimes = 0;
}
