package xyz.turtlecase.robot.business.task.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.constraints.NotNull;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.Record;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.turtlecase.robot.business.dongdu.DongduApi;
import xyz.turtlecase.robot.business.task.dto.*;
import xyz.turtlecase.robot.business.task.mapper.TaskInfoPo;
import xyz.turtlecase.robot.business.task.mapper.TaskProcessLogMapper;
import xyz.turtlecase.robot.business.task.mapper.TaskProcessMapper;
import xyz.turtlecase.robot.business.task.mapper.TaskProcessPo;
import xyz.turtlecase.robot.business.task.model.CallbackData;
import xyz.turtlecase.robot.business.task.model.CallbackType;
import xyz.turtlecase.robot.business.task.model.TaskStatus;
import xyz.turtlecase.robot.infra.exception.BaseException;
import xyz.turtlecase.robot.infra.model.BaseStatusEnum;
import xyz.turtlecase.robot.infra.service.RedisUtil;
import xyz.turtlecase.robot.infra.utils.BeanCopy;
import xyz.turtlecase.robot.infra.utils.CollectionUtils;
import xyz.turtlecase.robot.infra.utils.JsonUtils;
import xyz.turtlecase.robot.mq.MQMetaEnum;

/**
 * 任务过程服务实现
 */
@Slf4j
@Service
public class TaskProcessServiceImpl implements TaskProcessService {
    @Autowired
    private TaskProcessMapper taskProcessMapper;
    @Autowired
    private TaskProcessLogMapper taskProcessLogMapper;
    @Autowired
    private TaskCallbackLogService taskCallbackLogService;
    @Autowired
    private DongduApi dongduApi;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * task 结算
     *
     * @param taskId
     * @throws BaseException
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void settleTask(Integer taskId) throws BaseException {
        List<TaskProcessDTO> taskProcessDTOS = castTaskInfo(taskId);

        for (TaskProcessDTO taskProcessDTO : taskProcessDTOS) {
            Record<String, String> record = StreamRecords.objectBacked(JsonUtils.objectToJson(taskProcessDTO))
                    .withStreamKey(MQMetaEnum.STREAM_TOPIC_TASK_PROCESS.getTopic());
            redisUtil.streamAddRecord(record);
        }

        // 修改原始主任务状态 1=已计算, 2=未结算, 3=正在结算, 4=已校验
        taskProcessMapper.updateTaskStatus(taskId, AddonTaskStatus.CHECKING.getStatus());
    }

    /**
     * 过滤task process
     *
     * @param taskId
     * @return
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public List<TaskProcessDTO> castTaskInfo(Integer taskId) {
        List<TaskInfoPo> taskInfoPos = taskProcessMapper.selectTaskInfo(taskId);
        List<TaskProcessDTO> taskProcessDTOS = new ArrayList();

        for (TaskInfoPo taskInfoPo : taskInfoPos) {
            if (!new Integer(2).equals(taskInfoPo.getStatus())) {
                throw new BaseException("Task is checking or done");
            }

            TaskProcessPo taskProcessPo = taskInfoPo.castTaskProcess();
            taskProcessDTOS.add(BeanCopy.copyBean(taskProcessPo, TaskProcessDTO.class));
            taskProcessMapper.insert(taskProcessPo);
        }

        return taskProcessDTOS;
    }

    /**
     * 查询子任务
     *
     * @param taskId
     * @param subTaskId
     * @return
     */
    @Override
    public TaskProcessDTO getTaskProcess(@NotNull Integer taskId, @NotNull Integer subTaskId) {
        TaskProcessPo query = new TaskProcessPo();
        query.setTaskId(taskId);
        query.setSubTaskId(subTaskId);
        return BeanCopy.copyBean(taskProcessMapper.selectOne(query), TaskProcessDTO.class);
    }

    /**
     * 查询所有子任务
     *
     * @param taskId
     * @return
     */
    @Override
    public List<TaskProcessDTO> getTaskProcess(Integer taskId) {
        return BeanCopy.copyBeans(taskProcessMapper.selectTaskProcess(taskId), TaskProcessDTO.class);
    }

    /**
     * 更新子任务状态
     *
     * @param taskId
     * @param subTaskId
     * @param taskStatus
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void updateTaskStatus(@NotNull Integer taskId, @NotNull Integer subTaskId, @NotNull TaskStatus taskStatus) {
        log.info("update task process status taskId: {}, subTaskId:{}, status:{}", new Object[]{taskId, subTaskId, taskStatus});
        taskProcessMapper.updateTaskProcessStatus(taskId, subTaskId, taskStatus.getStatus());
    }

    /**
     * 重启未完成的任务
     */
    @Override
    public void restartUncompletedProcess() {
        TaskProcessPo query = new TaskProcessPo();
        query.setStatus(BaseStatusEnum.DISABLE.getValue());
        List<TaskProcessPo> taskProcessPoList = taskProcessMapper.select(query);
        if (CollectionUtils.isEmpty( taskProcessPoList)) {
            log.info("no uncompleted task process");
        }

        // 发送到MQ, 异步处理
        for (TaskProcessPo taskProcessPo : taskProcessPoList) {
            TaskProcessDTO taskProcessDTO = BeanCopy.copyBean(taskProcessPo, TaskProcessDTO.class);
            String json = JsonUtils.objectToJson(taskProcessDTO);
            Record<String, String> record = StreamRecords.objectBacked(json)
                    .withStreamKey(MQMetaEnum.STREAM_TOPIC_TASK_PROCESS.getTopic());
            redisUtil.streamAddRecord(record);
            log.info("re-send task process: {}", json);
        }
    }

    /**
     * @param taskId
     * @param subTaskId
     * @param twitterUserNames
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void batchAddLog(@NotNull Integer taskId, @NotNull Integer subTaskId, @NotNull Set<String> twitterUserNames) {
        if (!CollectionUtils.isEmpty(twitterUserNames)) {
            taskProcessLogMapper.batchInsert(taskId, subTaskId, new Date(), twitterUserNames);
        }
    }

    /**
     * 异步发送回调消息
     *
     * @param taskId
     */
    @Override
    public void sengCallbackMsg(@NotNull Integer taskId) {
        CallbackData callbackData = new CallbackData();
        callbackData.setType(CallbackType.TASK_COMPLETE_CALL_BACK.name());
        Map<String, Object> data = new HashMap();
        data.put("taskId", taskId);
        callbackData.setData(data);
        log.info("send to task call back msg, task id: {}", taskId);
        Record<String, String> record = StreamRecords.objectBacked(JsonUtils.objectToJson(callbackData))
                .withStreamKey(MQMetaEnum.STREAM_TOPIC_TASK_COMPLETE_CALLBACK.getTopic());
        redisUtil.streamAddRecord(record);
    }

    /**
     * 检查task是否完成
     * @param taskId
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void checkTaskComplete(@NotNull Integer taskId) {
        TaskCallbackLogDTO taskCallbackLogDTO = taskCallbackLogService.getByTaskId(taskId);

        // 有记录则表明已经调用过, 不需要再次执行
        if(taskCallbackLogDTO != null){
            return;
        }

        List<TaskProcessDTO> taskProcessDTOS = getTaskProcess(taskId);

        // 若无任务执行记录
        if(CollectionUtils.isEmpty(taskProcessDTOS)){
            return;
        }

        Boolean done = Boolean.TRUE;

        for(TaskProcessDTO taskProcessDTO : taskProcessDTOS){
            // 如果有非完成任务的状态
            if (!TaskStatus.DONE.getStatus().equals(taskProcessDTO.getStatus())) {
                log.info("task process is running, taskId {} subTaskId {}, status {}", taskProcessDTO.getTaskId(), taskProcessDTO.getSubTaskId(), taskProcessDTO.getStatus());
                done = Boolean.FALSE;
                break;
            }
        }

        // 若整体任务完成
        if (done) {

            // 全部任务已完成, 过滤出所有任务都有的twitter账号, 写到robot_task_result表
            taskProcessLogMapper.completeTask(taskId, new Date(), taskProcessDTOS.size());

            // 修改主任务状态
            taskProcessMapper.updateTaskStatus(taskId, AddonTaskStatus.CHECKED.getStatus());

            // 异步回调webapp, 通知结算
            sengCallbackMsg(taskId);
        }
    }

    /**
     * 执行task任务
     * @param taskId
     * @param subTaskId
     * @throws Exception
     */
    @Override
    public void doTask(@NotNull Integer taskId, @NotNull Integer subTaskId) throws Exception {
        TaskProcessDTO taskProcessDTO = getTaskProcess(taskId, subTaskId);
        TaskType taskType = TaskType.valueOf(taskProcessDTO.getAction());
        TaskServiceFactory.getTaskService(taskType).execute(taskProcessDTO);
    }
}
