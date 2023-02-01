package xyz.turtlecase.robot.business.task.mapper;

import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import xyz.turtlecase.robot.business.task.model.TaskProcess;

/**
 * task任务进度表
 */
@Data
@EqualsAndHashCode
@Table(name = "robot_task_process")
public class TaskProcessPo extends TaskProcess {

}
