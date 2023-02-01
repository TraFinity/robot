package xyz.turtlecase.robot.infra.service;

import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.turtlecase.robot.infra.exception.RateLimitingException;

/**
 * 限流服务实现
 */
@Slf4j
@Service
public class RateLimitServiceImpl implements RateLimitService {
    /**
     * cache前缀
     */
    private static final String PREFIX = "rate_limit_request_";
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 检查是否限流
     *
     * @param requestKey 唯一请示标记
     * @throws RateLimitingException
     */
    public void checkRateLimit(String requestKey) throws RateLimitingException {
        if (StringUtils.isBlank(requestKey)) {
            return;
        }

        String key = PREFIX + requestKey;
        if (redisUtil.hasKey(key)) {
            log.info("request[{}] rate limit", requestKey);
            throw new RateLimitingException(requestKey);
        }
    }

    /**
     * 设置限流标记
     *
     * @param requestKey 唯一请示标记
     * @param value      值
     * @param expireMils 过期毫秒数
     */
    public void setRateLimit(String requestKey, String value, long expireMils) {
        // 默认15分钟(毫秒数)时间窗口
        long expire = 1000L * 60L * 15L;
        String t = "15m";

        // 单独为list add member设置24小时窗口, twitter对list添加成员限流有bug, 一约束就是一天
        if (StringUtils.equalsIgnoreCase("rate_limit_twitter_list_member_add", requestKey)) {
            expire = 1000L * 60L * 25L;
            t = "25h";
        }

        log.info("rate limit set {} expire millis {} ({}) ", new Object[]{requestKey, expire, t});
        String key = PREFIX + requestKey;
        this.redisUtil.setKey(key, value, expire, TimeUnit.MILLISECONDS);
    }
}
