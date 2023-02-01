package xyz.turtlecase.robot.business.task.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务信息
 */
@Data
@EqualsAndHashCode
public class TaskInfo {
    /**
     * task ID
     */
    private Integer taskId;
    /**
     * 子task ID
     */
    private Integer subTaskId;
    /**
     * 类型
     */
    private Integer type;
    /**
     * 合约地址
     */
    private String contract;
    /**
     * 任务类型动作
     */
    private Integer action;
    /**
     * 用户输入值, 需要标准化处理, 抽取数据
     */
    private String value;
    /**
     * 是否针对所有用户
     */
    private Integer allMembers;
    /**
     * 状态
     */
    private Integer status;

}
