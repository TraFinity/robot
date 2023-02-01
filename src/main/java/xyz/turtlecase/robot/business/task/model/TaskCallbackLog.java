package xyz.turtlecase.robot.business.task.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import xyz.turtlecase.robot.infra.model.BaseModel;

/**
 * 回调日志基类
 */
@Data
@EqualsAndHashCode
public class TaskCallbackLog extends BaseModel {
    private Integer taskId;
    /**
     * 回调url
     * todo 后面要换成服务注册发现
     */
    private String callbackUrl;
    /**
     * 尝试次数
     */
    private Integer retryTimes;
    /**
     * 最终结果
     */
    private Integer result;
    /**
     * 任务状态
     */
    private Integer jobStatus;
}
