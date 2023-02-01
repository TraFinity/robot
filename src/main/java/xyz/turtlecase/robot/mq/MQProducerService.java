package xyz.turtlecase.robot.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MQProducerService {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送消息到MQ
     * @param topic
     * @param value
     */
    public void sendMsg(String topic, Object value) {
        if (!StringUtils.isBlank(topic)) {
            if (value != null) {
                redisTemplate.convertAndSend(topic, value);
            }
        }
    }
}
