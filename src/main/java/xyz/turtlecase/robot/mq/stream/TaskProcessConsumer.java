package xyz.turtlecase.robot.mq.stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Service;
import xyz.turtlecase.robot.business.task.dto.TaskProcessDTO;
import xyz.turtlecase.robot.business.task.dto.TaskType;
import xyz.turtlecase.robot.business.task.service.TaskService;
import xyz.turtlecase.robot.business.task.service.TaskServiceFactory;
import xyz.turtlecase.robot.infra.exception.RateLimitingException;
import xyz.turtlecase.robot.infra.service.RedisUtil;
import xyz.turtlecase.robot.infra.utils.JsonUtils;
import xyz.turtlecase.robot.mq.MQMetaEnum;

import java.io.IOException;

/**
 * task执行消息
 */
@Slf4j
@Service("taskProcessConsumer")
public class TaskProcessConsumer implements StreamListener<String, ObjectRecord<String, String>> {
    @Autowired
    private RedisUtil redisUtil;

    public void onMessage(ObjectRecord<String, String> message) {
        String stream =  message.getStream();
        RecordId recordId = message.getId();
        String v =  message.getValue();
        Boolean result = Boolean.FALSE;
        log.info("[auto ack] group:[{}] consumerName[{}] stream:[{}], id:[{}], value:[{}]",
                MQMetaEnum.STREAM_TOPIC_TASK_PROCESS.getGroup(),
                MQMetaEnum.STREAM_TOPIC_TASK_PROCESS.getConsumer(), stream, recordId, v);

        try {
            Thread.sleep(1000L);
            TaskProcessDTO taskProcessDTO = JsonUtils.jsonToPojo(v, TaskProcessDTO.class);
            if (taskProcessDTO == null) {
                return;
            }

            TaskType taskType = TaskType.valueOf(taskProcessDTO.getAction());
            TaskService taskService = TaskServiceFactory.getTaskService(taskType);
            if (taskService != null) {
                taskService.execute(taskProcessDTO);
                result = Boolean.TRUE;
                return;
            }

            log.warn("unsupported task type: {}, taskId:{}, subTaskId:{} ",
                    taskProcessDTO.getAction(),
                    taskProcessDTO.getTaskId(), taskProcessDTO.getSubTaskId());
            result = Boolean.TRUE;
        } catch (RateLimitingException e) {
            log.error("redis stream taskProcessConsumer twitter api rate limit error, value: ", v, e);
        } catch (IOException e) {
            log.error("redis stream taskProcessConsumer io error value: ", v, e);
        } catch (Exception e) {
            log.error("redis stream consumer error, value: ", v, e);
        } finally {
            redisUtil.streamDelete(stream, recordId.getValue());
        }

    }
}
