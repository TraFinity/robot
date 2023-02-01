package xyz.turtlecase.robot.infra.service;

import io.lettuce.core.RedisException;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.stream.Record;
import org.springframework.data.redis.connection.stream.StreamInfo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisUtil {
    private static final Logger log = LoggerFactory.getLogger(RedisUtil.class);
    @Resource(name = "redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 判断key是否存在
     *
     * @param key
     * @return
     */
    public boolean hasKey(String key) {
        try {
            Boolean hasKey = redisTemplate.hasKey(key);
            return null != hasKey ? hasKey : false;
        } catch (Exception var3) {
            log.error(var3.getMessage(), var3);
            return false;
        }
    }

    /**
     * 添加key缓存
     *
     * @param key
     * @param value
     */
    public void setKey(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception var4) {
            log.error(var4.getMessage(), var4);
        }

    }

    /**
     * 添加带过期时间的缓存
     *
     * @param key
     * @param value
     * @param time
     * @param unit
     */
    public void setKey(String key, Object value, long time, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, time, unit);
        } catch (Exception var7) {
            log.error(var7.getMessage(), var7);
        }

    }

    /**
     * 获取Key缓存值
     *
     * @param key
     * @param <T>
     * @return
     */
    public <T> T getKey(String key) {
        try {
            Object object = redisTemplate.opsForValue().get(key);
            return (T) object;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 删除keys缓存
     * todo 在redis集群中执行会失败
     *
     * @param keys
     */
    public void removeKey(String... keys) {
        if (null != keys && keys.length > 0) {
            try {
                if (keys.length == 1) {
                    redisTemplate.delete(keys[0]);
                } else {
                    redisTemplate.delete(Arrays.asList(keys));
                }
            } catch (Exception var3) {
                log.error(var3.getMessage(), var3);
            }
        }

    }

    /**
     * 指定过期时间
     *
     * @param key
     * @param time
     * @param unit
     */
    public void expire(String key, long time, TimeUnit unit) {
        try {
            if (time > 0L) {
                redisTemplate.expire(key, time, unit);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    /**
     * 获取过期时间
     *
     * @param key
     * @param unit
     * @return
     */
    public long expire(String key, TimeUnit unit) {
        try {
            Long expire = redisTemplate.getExpire(key, unit);
            return null != expire ? expire : 0L;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * 删除以keyStartWith开始的缓存
     * todo 在redis集群中执行会无效, 待定位是查询key,还是删除无效
     *
     * @param keyStartWith
     */
    public void deleteStartWith(String keyStartWith) {
        if (!StringUtils.isBlank(keyStartWith)) {
            try {
                Set<String> keys = redisTemplate.keys(keyStartWith.concat("*"));
                redisTemplate.delete(keys);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

        }
    }

    /**
     * 流处理, 创建消费组
     *
     * @param streamKey
     * @param group
     */
    public void streamCreateGroup(String streamKey, String group) {
        // 遍历检验分组是否存在
        boolean consumerExist = false;
        StreamInfo.XInfoGroups infoGroups = null;
        StreamInfo.XInfoStream xInfoStream = null;

        try {
            xInfoStream = stringRedisTemplate.opsForStream().info(streamKey);
            if (xInfoStream != null) {
                // 获取所有分组信息
                infoGroups = stringRedisTemplate.opsForStream().groups(streamKey);

                if (Objects.nonNull(infoGroups)) {
                    if (infoGroups.stream().anyMatch(t -> Objects.equals(group, t.groupName()))) {
                        consumerExist = true;
                    }
                }
            }
        } catch (RedisException | InvalidDataAccessApiUsageException | RedisSystemException var10) {
            log.info("fail to check redis stream group, stream [{}], group [{}]", streamKey, group);
        } finally {
            // 创建不存在的分组
            if (!consumerExist) {
                stringRedisTemplate.opsForStream().createGroup(streamKey, group);
            }

        }

    }

    /**
     * 获取stream下的所有分组
     *
     * @param streamKey
     * @return
     */
    public StreamInfo.XInfoGroups streamGetGroups(String streamKey) {
        return stringRedisTemplate.opsForStream().groups(streamKey);
    }

    /**
     * 添加map
     *
     * @param key
     * @param value
     * @return
     */
    public String streamAddMap(String key, Map<String, String> value) {
        return stringRedisTemplate.opsForStream().add(key, value).getValue();
    }

    /**
     * 添加record
     *
     * @param record
     * @return
     */
    public String streamAddRecord(Record<String, String> record) {
        return stringRedisTemplate.opsForStream().add(record).getValue();
    }

    /**
     * 确认消费
     *
     * @param key
     * @param group
     * @param recordId
     * @return
     */
    public Long streamAck(String key, String group, String... recordId) {
        return stringRedisTemplate.opsForStream().acknowledge(key, group, recordId);
    }

    /**
     * 删除消息
     * todo: 所有调用这个方法的地方, 需要改造, 做成异步重新读取任务后, 再投递
     *
     * @param key
     * @param recordId
     * @return
     */
    public Long streamDelete(String key, String... recordId) {
        return stringRedisTemplate.opsForStream().delete(key, recordId);
    }
}
