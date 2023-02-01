package xyz.turtlecase.robot.business.twitter.api;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.turtlecase.robot.business.twitter.api.dto.TwitterHttpError;
import xyz.turtlecase.robot.infra.exception.BaseException;
import xyz.turtlecase.robot.infra.service.RedisLockUtil;
import xyz.turtlecase.robot.infra.utils.http.okhttp.OkHttpErrorHandler;
import xyz.turtlecase.robot.infra.utils.http.okhttp.OkHttpResponse;

@Slf4j
@Component
public class TwitterErrorHandler implements OkHttpErrorHandler {
    @Autowired
    private RedisLockUtil redisLockUtil;

    public void errorHandler(OkHttpResponse response) {
        if (!Boolean.TRUE.equals(response.getSuccess())) {
            String body = response.getBody();
            TwitterHttpError httpError = JSON.parseObject(body, TwitterHttpError.class);
            log.error("call twitter api, status {},  body {}", response.getHttpStatus(), body);
            if (httpError == null) {
                throw new BaseException("network error");
            } else {
                throw new BaseException(httpError.getDetail());
            }
        }
    }
}
