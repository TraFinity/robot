package xyz.turtlecase.robot.business.task.model;

/**
 * 任务状态, 需要与webapp里面的一致
 */
public enum TaskStatus {
    PROCESSING(0),
    DONE(1),
    FAILURE(2),
    CANCEL(3);

    private Integer status;

    TaskStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return this.status;
    }
}
