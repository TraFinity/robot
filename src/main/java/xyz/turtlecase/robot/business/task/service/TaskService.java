package xyz.turtlecase.robot.business.task.service;

import java.util.Set;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import xyz.turtlecase.robot.business.task.dto.TaskProcessDTO;
import xyz.turtlecase.robot.business.task.dto.TaskType;

/**
 * task任务检查
 */
@Validated
public interface TaskService {
    /**
     * 任务类型
     * @return
     */
    TaskType getTaskType();

    /**
     * 执行子task任务, 过滤出完成任务的twitter账号
     * @param taskProcess
     * @return
     * @throws Exception
     */

    Set<String> twitterUserFilter(@NotNull TaskProcessDTO taskProcess) throws Exception;

    /**
     * 任务执行
     * @param taskProcess
     * @throws Exception
     */
    void execute(@NotNull TaskProcessDTO taskProcess) throws Exception;
}
