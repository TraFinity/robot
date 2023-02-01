package xyz.turtlecase.robot.infra.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

/**
 * cache manage配置
 */
@Configuration
@ConfigurationProperties(
        prefix = "spring.cache.redis"
)
public class RedisCacheManagerConfig {
    private Duration timeToLive = Duration.ofHours(24L);

    /**
     * 自定缓存key生成策略
     *
     * @return
     */
    @Bean
    public KeyGenerator firstKeyGenerator() {
        return (target, method, params) -> {
            return params[0].toString();
        };
    }

    /**
     * 自定义缓存key生成策略
     *
     * @return
     */
    @Bean
    public KeyGenerator commonKeyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(".");
            sb.append(method.getName());
            sb.append("#");

            for (Object obj : params) {
                sb.append(obj.toString());
            }

            return sb.toString();
        };
    }

    /**
     * 自定义, 主动设置序列化, 解决乱码问题
     *
     * @param factory
     * @return
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        // 解决查询缓存转换异常的问题
        ObjectMapper om = new ObjectMapper();
        // 不转换final类
        om.activateDefaultTyping(om.getPolymorphicTypeValidator(), DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        // 配置序列化(解决乱码问题)
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                .entryTtl(this.timeToLive);
        Map<String, RedisCacheConfiguration> redisExpireConfig = new HashMap();
        // 这里设置一个一分钟超时配置,
        redisExpireConfig.put("1min", RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
                .entryTtl(this.timeToLive));
        RedisCacheManager redisCacheManager = RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(factory))
                .cacheDefaults(config)
                .withInitialCacheConfigurations(redisExpireConfig)
                .transactionAware()
                .build();
        return redisCacheManager;
    }
}
