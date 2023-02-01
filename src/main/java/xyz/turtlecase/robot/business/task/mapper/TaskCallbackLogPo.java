package xyz.turtlecase.robot.business.task.mapper;

import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import xyz.turtlecase.robot.business.task.model.TaskCallbackLog;

/**
 * task回调日志记录PO
 */
@Data
@EqualsAndHashCode
@Table(name = "robot_task_callback_log")
public class TaskCallbackLogPo extends TaskCallbackLog {

}
