package xyz.turtlecase.robot.business.task.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * task DTO
 */
@Data
@EqualsAndHashCode
public class TaskDTO {
    /**
     * 任务类型
     */
    private TaskType taskType;

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * tweet ID
     */
    private String tweetId;

    /**
     * 源推ID
     */
    private String sourceUserName;

    /**
     * 目标twitter ID
     */
    private String targetUserName;

    /**
     * 批量follow的源账号名
     */
    private List<String> sourceUserNames;

    /**
     * 批量follow的目标账号名
     */
    private List<String> targetUserNames;
}
