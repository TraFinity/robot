package xyz.turtlecase.robot.mq.stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.Record;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Service;
import xyz.turtlecase.robot.business.task.service.TaskProcessService;
import xyz.turtlecase.robot.infra.service.RedisUtil;
import xyz.turtlecase.robot.mq.MQMetaEnum;

/**
 * task执行消息
 */
@Slf4j
@Service("taskCompleteCheckerConsumer")
public class TaskCompleteCheckerConsumer implements StreamListener<String, ObjectRecord<String, String>> {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private TaskProcessService taskProcessService;

    @Override
    public void onMessage(ObjectRecord<String, String> message) {
        String stream = message.getStream();
        RecordId recordId = message.getId();
        String v = message.getValue();
        Boolean result = Boolean.FALSE;
        log.info("[auto ack] group:[{}] consumerName[{}] stream:[{}], id:[{}], value:[{}]",
                MQMetaEnum.STREAM_TOPIC_TASK_COMPLETE_CHECK.getGroup(),
                MQMetaEnum.STREAM_TOPIC_TASK_COMPLETE_CHECK.getConsumer(), stream, recordId, v);

        try{
            // 休眠5s, 防止任务执行太快导致twitter封
            Thread.sleep(5000L);
            taskProcessService.checkTaskComplete(Integer.valueOf(v));
            result = Boolean.TRUE;
        }catch (Exception e){
            log.error("redis stream consumer error, value: {} ", v, e);
        }finally {
            redisUtil.streamDelete(stream, recordId.getValue());

            // 如果消费不成功, 重新投递
            if (!result) {
                Record<String, String> record = StreamRecords.objectBacked(v).withStreamKey(MQMetaEnum.STREAM_TOPIC_TASK_COMPLETE_CHECK.getTopic());
                redisUtil.streamAddRecord(record);
            }
        }
    }
}
