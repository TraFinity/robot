package xyz.turtlecase.robot.business.task.service;

import javax.validation.constraints.NotNull;

import xyz.turtlecase.robot.business.task.dto.TaskCallbackLogDTO;

public interface TaskCallbackLogService {
    TaskCallbackLogDTO getByTaskId(@NotNull Integer taskId);

    void create(TaskCallbackLogDTO taskCallbackLogDTO);

    void update(TaskCallbackLogDTO taskCallbackLogDTO);
}
