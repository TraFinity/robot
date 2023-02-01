package xyz.turtlecase.robot.mq.stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.Record;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Service;
import xyz.turtlecase.robot.business.dongdu.DongduApi;
import xyz.turtlecase.robot.business.task.dto.TaskCallbackLogDTO;
import xyz.turtlecase.robot.business.task.model.CallbackData;
import xyz.turtlecase.robot.business.task.service.TaskCallbackLogService;
import xyz.turtlecase.robot.infra.model.BaseStatusEnum;
import xyz.turtlecase.robot.infra.service.RedisUtil;
import xyz.turtlecase.robot.infra.utils.JsonUtils;
import xyz.turtlecase.robot.mq.MQMetaEnum;

/**
 * 异步消息回调消费
 */
@Slf4j
@Service("callBackConsumer")
public class CallBackConsumer implements StreamListener<String, ObjectRecord<String, String>> {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private DongduApi dongduApi;
    @Autowired
    private TaskCallbackLogService taskCallbackLogService;

    @Override
    public void onMessage(ObjectRecord<String, String> message) {
        String stream = message.getStream();
        RecordId recordId = message.getId();
        String v = message.getValue();
        Boolean result = Boolean.FALSE;
        Boolean outOfMaxRetryTimes = Boolean.FALSE;
        log.info("[auto ack] group:[{}] consumerName[{}] stream:[{}], id:[{}], value:[{}]",
                MQMetaEnum.STREAM_TOPIC_TASK_COMPLETE_CALLBACK.getGroup(),
                MQMetaEnum.STREAM_TOPIC_TASK_COMPLETE_CALLBACK.getConsumer(), stream, recordId, v);
        TaskCallbackLogDTO taskCallbackLogDTO = null;

        try {
            // 特意堵塞, 不要太多并发
            Thread.sleep(1000L);
            CallbackData callbackData = JsonUtils.jsonToPojo(v, CallbackData.class);
            if (callbackData == null) {
                return;
            }
            v = JsonUtils.objectToJson(callbackData);
            Object _taskId = callbackData.getData().get("taskId");

            if (_taskId == null) {
                log.error("task id id null");
                return;
            }

            Integer taskId = (Integer) _taskId;
            taskCallbackLogDTO = taskCallbackLogService.getByTaskId(taskId);
            if (taskCallbackLogDTO == null) {
                taskCallbackLogDTO = new TaskCallbackLogDTO();
                taskCallbackLogDTO.setTaskId(taskId);
                taskCallbackLogDTO.setCallbackUrl(dongduApi.getCallbackUrl(taskId));
                taskCallbackLogDTO.setRetryTimes(0);
            } else {
                taskCallbackLogDTO.setRetryTimes(taskCallbackLogDTO.getRetryTimes() + 1);
                // 如果任务已结束, 则不再执行
                if (BaseStatusEnum.ENABLE.getValue().equals(taskCallbackLogDTO.getJobStatus())) {
                    result = Boolean.TRUE;
                    return;
                }

                // 已大于最大尝试次数, 结束
                if (taskCallbackLogDTO.getRetryTimes() > callbackData.getMaxRetryTimes()) {
                    result = Boolean.TRUE;
                    outOfMaxRetryTimes = Boolean.TRUE;
                    taskCallbackLogDTO.setResult(BaseStatusEnum.DISABLE.getValue());
                    taskCallbackLogDTO.setJobStatus(BaseStatusEnum.DISABLE.getValue());
                    log.error("call back taskId {}  retry {} > maxRetryTimes [{}], cancel job",
                            taskId, taskCallbackLogDTO.getRetryTimes(), callbackData.getMaxRetryTimes());
                    return;
                }
            }

            dongduApi.taskCallback((Integer) callbackData.getData().get("taskId"));

            // 执行顺利
            taskCallbackLogDTO.setResult(BaseStatusEnum.ENABLE.getValue());
            taskCallbackLogDTO.setJobStatus(BaseStatusEnum.ENABLE.getValue());
            result = Boolean.TRUE;
        } catch (Exception e) {
            log.error("redis stream consumer error , value: {}", v, e);
            result = Boolean.FALSE;
            taskCallbackLogDTO.setResult(BaseStatusEnum.DISABLE.getValue());
            taskCallbackLogDTO.setJobStatus(BaseStatusEnum.DISABLE.getValue());

        } finally {

            // 更新db
            if (taskCallbackLogDTO.getId() == null) {
                taskCallbackLogService.create(taskCallbackLogDTO);
            } else if (!outOfMaxRetryTimes) {
                // 未超过最大重试次数才更新
                taskCallbackLogService.update(taskCallbackLogDTO);
            }

            // 删除并重新发送call back消息
            redisUtil.streamDelete(stream, recordId.getValue());
            if (!result) {
                Record<String, String> record = StreamRecords.objectBacked(v)
                        .withStreamKey(MQMetaEnum.STREAM_TOPIC_TASK_COMPLETE_CALLBACK.getTopic());
                redisUtil.streamAddRecord(record);
                log.info("re push to steam for callback with taskId {}", taskCallbackLogDTO.getTaskId());
            } else {
                log.info("finish to callback for taskId {}", taskCallbackLogDTO.getTaskId());
            }
        }
    }
}
