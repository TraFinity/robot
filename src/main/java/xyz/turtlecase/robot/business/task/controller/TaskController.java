package xyz.turtlecase.robot.business.task.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import xyz.turtlecase.robot.business.task.service.TaskProcessService;
import xyz.turtlecase.robot.infra.constant.Constants;
import xyz.turtlecase.robot.infra.web.BaseController;
import xyz.turtlecase.robot.infra.web.RestResult;

/**
 * 任务校验接口
 */
@Slf4j
@RestController
public class TaskController extends BaseController {
    @Autowired
    private TaskProcessService taskProcessService;

    /**
     * 发起task校验
     *
     * @param taskId 任务ID
     * @return
     */
    @PostMapping({"/task/{taskId}/settle"})
    @ResponseBody
    public RestResult<String> settleTask(@PathVariable(name = "taskId") Integer taskId) {
        taskProcessService.settleTask(taskId);
        return Constants.RESPONSE_SUCCESS;
    }

    /**
     * 发起子任务校验
     *
     * @param taskId    主任务ID
     * @param subTaskId 子任务ID
     * @return
     * @throws Exception
     */
    @PostMapping({"/task/{taskId}/{subTaskId}/execute"})
    @ResponseBody
    public RestResult<String> doSubTask(@PathVariable(name = "taskId") Integer taskId,
                                        @PathVariable(name = "subTaskId") Integer subTaskId) throws Exception {
        taskProcessService.doTask(taskId, subTaskId);
        return Constants.RESPONSE_SUCCESS;
    }

    /**
     * task完成检查
     *
     * @param taskId 主task ID
     * @return
     * @throws Exception
     */
    @PostMapping({"/task/{taskId}/completeCheckTask"})
    @ResponseBody
    public RestResult<String> completeCheckTask(@PathVariable(name = "taskId") Integer taskId) throws Exception {
        taskProcessService.checkTaskComplete(taskId);
        return Constants.RESPONSE_SUCCESS;
    }

    /**
     * 重启未完成的任务
     *
     * @return
     */
    @PostMapping({"/task/restartUncompletedProcess"})
    @ResponseBody
    public RestResult<String> restartUncompletedProcess() {
        taskProcessService.restartUncompletedProcess();
        return Constants.RESPONSE_SUCCESS;
    }
}
