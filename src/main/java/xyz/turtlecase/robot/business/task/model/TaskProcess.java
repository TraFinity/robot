package xyz.turtlecase.robot.business.task.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode
public class TaskProcess {
    /**
     * task ID
     */
    private Integer taskId;
    /**
     * sub task ID
     */
    private Integer subTaskId;
    /**
     * 机器人执行时间
     */
    private Date createTime;
    /**
     * 更新时间, 也可以视为结束时间, 结合status
     */
    private Date updateTime;
    /**
     * 合约地址
     */
    private String contract;
    /**
     * 主任务类型
     */
    private Integer type;
    /**
     * 子任务类型
     */
    private String action;
    /**
     * 值
     */
    private String value;
    /**
     * 是否所有成员
     */
    private Integer allMember;
    /**
     * 状态
     */
    private Integer status;
}
