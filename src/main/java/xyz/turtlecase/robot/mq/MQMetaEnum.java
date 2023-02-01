package xyz.turtlecase.robot.mq;

import org.apache.commons.lang3.StringUtils;
import xyz.turtlecase.robot.infra.utils.CommonUtil;
import xyz.turtlecase.robot.infra.utils.Env;

public enum MQMetaEnum {
    /**
     * 测试使用
     */
    STREAM_TOPIC_TEST("stream-topic-test", "group-test", "consumer-test"),
    /**
     * twitter账户同步到lit
     */
    STREAM_TOPIC_TWITTER_LIST_SYNC("stream-topic-twitter-list-member-sync", "group-list", "consumer-list"),
    /**
     * task任务执行
     */
    STREAM_TOPIC_TASK_PROCESS("stream-topic-task-process", "group-task-process", "consumer-task-process"),
    /**
     * task任务完成校验
     */
    STREAM_TOPIC_TASK_COMPLETE_CHECK("stream-topic-task-complete-check", "group-task-complete-check", "consumer-task-complete-check"),
    /**
     * 回调结算
     */
    STREAM_TOPIC_TASK_COMPLETE_CALLBACK("stream-topic-task-complete-callback", "group-task-complete-callback", "consumer-task-complete-callback");

    private String topic;
    private String group;
    private String consumer;

    MQMetaEnum(String topic, String group, String consumer) {
        assert StringUtils.isNotBlank(topic);
        assert StringUtils.isNotBlank(group);
        assert StringUtils.isNotBlank(consumer);

        // 非正式环境, 加多个标记, 区分开发与测试, 方便测试
        if (!Env.isPrdEnv()) {
            topic = topic + "_" + CommonUtil.getEnv("active_env");
        }

        this.topic = topic;
        this.group = group;
        this.consumer = consumer;
    }

    public String getTopic() {
        return this.topic;
    }

    public String getGroup() {
        return this.group;
    }

    public String getConsumer() {
        return this.consumer;
    }
}
