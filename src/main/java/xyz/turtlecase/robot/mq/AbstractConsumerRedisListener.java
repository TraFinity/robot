package xyz.turtlecase.robot.mq;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.transaction.annotation.Transactional;

/**
 * 同步钱包NFT信息到MQ消费
 */
public abstract class AbstractConsumerRedisListener implements MessageListener {

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void onMessage(Message message, byte[] pattern) {
        doAction(message);
    }

    /**
     * 获取topic信息
     * @return
     */
    public abstract String getTopic();

    /**
     * 业务执行
     * @param message
     */
    public abstract void doAction(Message message);
}
