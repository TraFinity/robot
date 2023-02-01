package xyz.turtlecase.robot.business.task.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.constraints.NotNull;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.stream.Record;
import org.springframework.data.redis.connection.stream.StreamRecords;
import xyz.turtlecase.robot.business.task.dto.TaskProcessDTO;
import xyz.turtlecase.robot.business.task.model.TaskStatus;
import xyz.turtlecase.robot.business.twitter.biz.service.TwitterBizService;
import xyz.turtlecase.robot.infra.service.RedisUtil;
import xyz.turtlecase.robot.infra.utils.CollectionUtils;
import xyz.turtlecase.robot.infra.utils.SpringBeanUtils;
import xyz.turtlecase.robot.mq.MQMetaEnum;

@Slf4j
public abstract class AbstractTaskService implements TaskService {

    /**
     * 任务执行
     *
     * @param taskProcess
     * @throws Exception
     */
    @Override
    public void execute(@NotNull TaskProcessDTO taskProcess) throws Exception {
        // 从spring bean加载对象, 此处处理过于笨重
        TaskProcessService taskProcessService = SpringBeanUtils.getBean(TaskProcessService.class);

        // 过滤出完成任务的twitter账号
        Set<String> twitterUsers = twitterUserFilter(taskProcess);

        //不再进行holder-twitter账号过滤, 全部落库, 交给sql过滤
        if (!CollectionUtils.isEmpty(twitterUsers)) {
            taskProcessService.batchAddLog(taskProcess.getTaskId(), taskProcess.getSubTaskId(), twitterUsers);
        }

        // 修改子任务状态
        taskProcessService.updateTaskStatus(taskProcess.getTaskId(), taskProcess.getSubTaskId(), TaskStatus.DONE);
        log.info("taskId {}, subTaskId {} done, send msg to complete check", taskProcess.getTaskId(), taskProcess.getSubTaskId());

        // 异步检查整体任务状态
        RedisUtil redisUtil = SpringBeanUtils.getBean(RedisUtil.class);
        Record<String, String> record = StreamRecords.objectBacked(taskProcess.getTaskId().toString()).withStreamKey(MQMetaEnum.STREAM_TOPIC_TASK_COMPLETE_CHECK.getTopic());
        redisUtil.streamAddRecord(record);
    }
}
