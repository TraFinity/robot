package xyz.turtlecase.robot.infra.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.ErrorHandler;
import xyz.turtlecase.robot.mq.NFTHolderSyncConsumerRedisListener;

/**
 * 使用Redis做为MQ的监听, 此处配置监听容器
 * 注意: 此处使用的是pub-sub方式, 仅用于holder同步, 其他的MQ监听使用了redis stream
 */
@Configuration
public class RedisMQConfig {
    @Autowired
    @Qualifier("nftHolderSyncConsumerRedisListener")
    private NFTHolderSyncConsumerRedisListener nftHolderSyncConsumerRedisListener;
    @Autowired
    @Qualifier("springSessionRedisTaskExecutor")
    private ThreadPoolTaskExecutor springSessionRedisTaskExecutor;

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(LettuceConnectionFactory lettuceConnectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(lettuceConnectionFactory);
        container.setTaskExecutor(this.springSessionRedisTaskExecutor);
        container.setSubscriptionExecutor(this.springSessionRedisTaskExecutor);
        container.addMessageListener(this.nftHolderSyncConsumerRedisListener, new ChannelTopic(this.nftHolderSyncConsumerRedisListener.getTopic()));
        container.setErrorHandler(new ErrorHandler() {
            private final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);

            public void handleError(Throwable t) {
                this.logger.error("redis message listen", t);
            }
        });
        return container;
    }
}
