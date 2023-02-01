package xyz.turtlecase.robot.mq.stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions;
import org.springframework.util.ErrorHandler;
import xyz.turtlecase.robot.infra.config.ConfigProperties;
import xyz.turtlecase.robot.infra.service.RedisUtil;
import xyz.turtlecase.robot.mq.MQMetaEnum;

import java.time.Duration;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Configuration
@DependsOn({"springSessionRedisTaskExecutor",
        "twitterListMemberSyncConsumer",
        "twitterListMemberSyncConsumer",
        "taskProcessConsumer",
        "taskCompleteCheckerConsumer",
        "callBackConsumer"})
public class RedisStreamConfiguration {
    @Autowired
    private ConfigProperties configProperties;
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    @Qualifier("twitterListMemberSyncConsumer")
    private TwitterListMemberSyncConsumer twitterListMemberSyncConsumer;
    @Autowired
    @Qualifier("taskProcessConsumer")
    private TaskProcessConsumer taskProcessConsumer;
    @Autowired
    @Qualifier("taskCompleteCheckerConsumer")
    private TaskCompleteCheckerConsumer taskCompleteCheckerConsumer;
    @Autowired
    @Qualifier("callBackConsumer")
    private CallBackConsumer callBackConsumer;

    @Bean(
            initMethod = "start",
            destroyMethod = "stop"
    )
    public StreamMessageListenerContainer<String, ObjectRecord<String, String>> streamMessageListenerContainer() {

        // 不管是否初始化, 启动时均检查并创建
        for (MQMetaEnum mqMetaEnum : MQMetaEnum.values()) {
            redisUtil.streamCreateGroup(mqMetaEnum.getTopic(), mqMetaEnum.getGroup());
        }

        AtomicInteger index = new AtomicInteger(1);
        int processors = MQMetaEnum.values().length;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(processors, processors + 4, 0L, TimeUnit.SECONDS, new LinkedBlockingDeque(), (r) -> {
            Thread thread = new Thread(r);
            thread.setName("async-stream-consumer-" + index.getAndIncrement());
            thread.setDaemon(true);
            log.info("init thread pool with thread: {}", thread.getName());
            return thread;
        });

        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, String>> options = StreamMessageListenerContainerOptions
                .builder()
                .targetType(String.class)
                .batchSize(3)
                .executor(executor)
                .pollTimeout(Duration.ofSeconds(50L))
                .errorHandler(new ErrorHandler() {
                    public void handleError(Throwable t) {

                        RedisStreamConfiguration.log.error("stream error", t);
                    }
                })
                .build();
        StreamMessageListenerContainer<String, ObjectRecord<String, String>> streamMessageListenerContainer = StreamMessageListenerContainer.create(redisConnectionFactory, options);

        // 同步holder twitter账户到list
        streamMessageListenerContainer.receiveAutoAck(Consumer.from(MQMetaEnum.STREAM_TOPIC_TWITTER_LIST_SYNC.getGroup(),
                MQMetaEnum.STREAM_TOPIC_TWITTER_LIST_SYNC.getConsumer()),
                StreamOffset.create(MQMetaEnum.STREAM_TOPIC_TWITTER_LIST_SYNC.getTopic(),ReadOffset.lastConsumed()),
                twitterListMemberSyncConsumer);

        // 任务校验
        streamMessageListenerContainer.receiveAutoAck(Consumer.from(MQMetaEnum.STREAM_TOPIC_TASK_PROCESS.getGroup(),
                MQMetaEnum.STREAM_TOPIC_TASK_PROCESS.getConsumer()),
                StreamOffset.create(MQMetaEnum.STREAM_TOPIC_TASK_PROCESS.getTopic(), ReadOffset.lastConsumed()),
                taskProcessConsumer);

        // 任务完成检查
        streamMessageListenerContainer.receiveAutoAck(Consumer.from(MQMetaEnum.STREAM_TOPIC_TASK_COMPLETE_CHECK.getGroup(),
                MQMetaEnum.STREAM_TOPIC_TASK_COMPLETE_CHECK.getConsumer()),
                StreamOffset.create(MQMetaEnum.STREAM_TOPIC_TASK_COMPLETE_CHECK.getTopic(), ReadOffset.lastConsumed()),
                taskCompleteCheckerConsumer);

        // 回调webapp
        streamMessageListenerContainer.receiveAutoAck(Consumer.from(MQMetaEnum.STREAM_TOPIC_TASK_COMPLETE_CALLBACK.getGroup(),
                MQMetaEnum.STREAM_TOPIC_TASK_COMPLETE_CALLBACK.getConsumer()),
                StreamOffset.create(MQMetaEnum.STREAM_TOPIC_TASK_COMPLETE_CALLBACK.getTopic(), ReadOffset.lastConsumed()),
                callBackConsumer);
        return streamMessageListenerContainer;
    }
}
