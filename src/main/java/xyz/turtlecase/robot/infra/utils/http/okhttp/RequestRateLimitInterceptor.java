package xyz.turtlecase.robot.infra.utils.http.okhttp;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.turtlecase.robot.infra.service.RateLimitService;
import xyz.turtlecase.robot.infra.utils.SpringBeanUtils;

import static xyz.turtlecase.robot.infra.constant.Constants.HTTP_REQUEST_LIMIT_HEADER;

/**
 * 请示限流拦截器
 */
@Slf4j
public class RequestRateLimitInterceptor implements Interceptor {
    private static final List<Integer> HTTP_RATE_LIMIT_STATUS = Arrays.asList(429, 88);

    @NotNull
    @Override
    public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        boolean mustSetLimit = false;
        // 15分钟一个时间窗口
        long expireMills = 1000L * 60L * 15L;
        String reset = "" + System.currentTimeMillis() + expireMills;

        String rateLimitRemaining = response.header("x-rate-limit-remaining");
        String limitKey = request.header(HTTP_REQUEST_LIMIT_HEADER);
        if (StringUtils.isNotBlank(rateLimitRemaining) && StringUtils.equals("10", rateLimitRemaining)) {
            mustSetLimit = true;
        }

        // 如果状态码是限流
        if (HTTP_RATE_LIMIT_STATUS.contains(response.code())) {
            if (StringUtils.isNotBlank(limitKey)) {
                reset = response.header("x-rate-limit-reset");
                if (StringUtils.isNotBlank(reset)) {
                    reset = reset + "000";
                    // 堵塞时间在reset后, 再加多2分钟
                    expireMills = Long.valueOf(reset) - System.currentTimeMillis() + 60000L;
                }
            }

            mustSetLimit = true;
        }

        if (mustSetLimit) {
            log.info("request-rate-limit, url: {}, response header: \n{}", request.url(), response.headers());
            RateLimitService rateLimitService = SpringBeanUtils.getBean(RateLimitService.class);
            rateLimitService.setRateLimit(limitKey, reset, expireMills);
            log.info("http request limit: request url: {}, http status: {}, key: {}  ", request.url(), response.code(), limitKey, response.headers());
        }

        return response;
    }
}
