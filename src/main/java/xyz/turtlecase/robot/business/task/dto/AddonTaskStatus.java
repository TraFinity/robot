package xyz.turtlecase.robot.business.task.dto;

/**
 * 原始任务状态
 */
public enum AddonTaskStatus {
    DONE(1),
    UNCHECK(2),
    CHECKING(3),
    CHECKED(4);

    private Integer status;

    AddonTaskStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return this.status;
    }
}
