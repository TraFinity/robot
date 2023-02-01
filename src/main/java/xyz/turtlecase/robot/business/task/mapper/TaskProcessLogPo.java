package xyz.turtlecase.robot.business.task.mapper;

import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import xyz.turtlecase.robot.business.task.model.TaskProcessLog;

/**
 * task任务日志记录
 */
@Data
@EqualsAndHashCode
@Table(name = "robot_task_process_log")
public class TaskProcessLogPo extends TaskProcessLog {

}
