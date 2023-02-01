package xyz.turtlecase.robot.business.task.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class TaskProcessLog {
    /**
     * task ID
     */
    private Integer taskId;
    /**
     * sub task ID
     */
    private Integer subTaskId;
    /**
     * 创建时间
     */
    private Data createTime;
    /**
     * twitter userName
     */
    private String twitterUserName;
}
