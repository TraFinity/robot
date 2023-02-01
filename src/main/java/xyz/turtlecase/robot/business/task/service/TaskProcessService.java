package xyz.turtlecase.robot.business.task.service;

import java.util.List;
import java.util.Set;
import javax.validation.constraints.NotNull;

import xyz.turtlecase.robot.business.task.dto.TaskProcessDTO;
import xyz.turtlecase.robot.business.task.model.TaskStatus;
import xyz.turtlecase.robot.infra.exception.BaseException;

/**
 * 任务过程服务
 */
public interface TaskProcessService {
    /**
     * 结算
     *
     * @param taskId
     * @throws BaseException
     */
    void settleTask(Integer taskId) throws BaseException;

    /**
     * 转换任务为TaskProcess
     *
     * @param taskId
     * @return
     */
    List<TaskProcessDTO> castTaskInfo(Integer taskId);

    /**
     * 加载任务过程记录
     *
     * @param taskId
     * @return
     */
    List<TaskProcessDTO> getTaskProcess(Integer taskId);

    /**
     * 查询子任务
     *
     * @param taskId
     * @param subTaskId
     * @return
     */
    TaskProcessDTO getTaskProcess(@NotNull Integer taskId, @NotNull Integer subTaskId);

    /**
     * 更新子任务状态
     *
     * @param taskId
     * @param subTaskId
     * @param taskStatus
     */
    void updateTaskStatus(Integer taskId, Integer subTaskId, TaskStatus taskStatus);

    /**
     * 执行子任务检查
     *
     * @param taskId
     * @param subTaskId
     * @throws Exception
     */
    void doTask(@NotNull Integer taskId, @NotNull Integer subTaskId) throws Exception;

    /**
     * 检查主任务完成状态
     *
     * @param taskId
     */
    void checkTaskComplete(@NotNull Integer taskId);

    /**
     * 发送回调消息
     *
     * @param taskId
     */
    void sengCallbackMsg(@NotNull Integer taskId);

    /**
     * 批量写任务执行日志记录
     *
     * @param taskId
     * @param subTaskId
     * @param twitterUserNames
     */
    void batchAddLog(@NotNull Integer taskId, @NotNull Integer subTaskId, @NotNull Set<String> twitterUserNames);

    /**
     * 将task process未完成的任务, 重新加载出来执行
     */
    void restartUncompletedProcess();
}
