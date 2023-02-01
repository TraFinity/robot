package xyz.turtlecase.robot.infra.service;

import xyz.turtlecase.robot.infra.exception.RateLimitingException;

/**
 * 限流服务接口(主要用于twitter)
 */
public interface RateLimitService {
    /**
     * 检查是否限流
     *
     * @param requestKey 唯一请示标记
     * @throws RateLimitingException
     */
    void checkRateLimit(String requestKey) throws RateLimitingException;

    /**
     * 设置限流标记
     *
     * @param requestKey 唯一请示标记
     * @param value      值
     * @param expireMils 过期毫秒数
     */
    void setRateLimit(String requestKey, String value, long expireMils);
}
