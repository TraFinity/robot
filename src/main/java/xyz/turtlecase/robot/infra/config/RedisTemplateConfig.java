package xyz.turtlecase.robot.infra.config;

import com.alibaba.fastjson2.support.spring.data.redis.GenericFastJsonRedisSerializer;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * redis配置
 */
@EnableCaching
@Configuration
@AutoConfigureBefore({RedisAutoConfiguration.class})
public class RedisTemplateConfig {
    @Bean(
            name = {"redisTemplate"}
    )
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory factory) {
        RedisSerializer redisSerializer = new GenericFastJsonRedisSerializer();
        // 配置序列化
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(redisSerializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(redisSerializer);
        redisTemplate.setConnectionFactory(factory);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean(
            name = {"springSessionRedisTaskExecutor"}
    )
    public ThreadPoolTaskExecutor springSessionRedisTaskExecutor() {
        int processors = Runtime.getRuntime().availableProcessors();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(processors);
        executor.setMaxPoolSize(processors + 1);
        executor.setKeepAliveSeconds(60);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("redis-executor-thread: ");
        return executor;
    }
}
