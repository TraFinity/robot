package xyz.turtlecase.robot.infra.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.connection.RedisStringCommands.SetOption;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;

/**
 * redis分布式锁
 */
@Component
public class RedisLockUtil {
    private static final byte[] SCRIPT_RELEASE_LOCK = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end".getBytes();
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 尝试获取分布式锁
     *
     * @param lockKey   键
     * @param requestId 请示ID, 用来判断重入锁, 最好每一个请示是唯一的
     * @param expire    锁有效时间(秒)
     * @return
     */
    public synchronized Boolean tryLock(String lockKey, String requestId, long expire) {

        return stringRedisTemplate.execute((RedisCallback<Boolean>) redisConnection -> redisConnection.set(lockKey.getBytes(),
                requestId.getBytes(), Expiration.from(expire, TimeUnit.SECONDS), RedisStringCommands.SetOption.SET_IF_ABSENT));
    }

    /**
     * 释放分布式锁
     *
     * @param lockKey   键
     * @param requestId 请示ID, 用于判定重入锁, 最好每一个请示是唯一的
     * @return
     */
    public synchronized Boolean releaseLock(String lockKey, String requestId) {

        return stringRedisTemplate.execute((RedisCallback<Boolean>) redisConnection ->
                redisConnection.eval(SCRIPT_RELEASE_LOCK, ReturnType.BOOLEAN, 1, lockKey.getBytes(), requestId.getBytes()));
    }
}
