package xyz.turtlecase.robot.business.twitter.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import xyz.turtlecase.robot.business.twitter.api.TwitterConfig;
import xyz.turtlecase.robot.business.twitter.api.auth.OAuth2AccessToken;
import xyz.turtlecase.robot.business.twitter.api.auth.TwitterCredentialsFactory;
import xyz.turtlecase.robot.business.twitter.api.auth.TwitterOAuth2Service;
import xyz.turtlecase.robot.business.twitter.api.auth.TwitterRefreshTokenRequestParam;
import xyz.turtlecase.robot.infra.config.ConfigProperties;
import xyz.turtlecase.robot.infra.service.RedisLockUtil;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class RefreshTokenJobTask {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String rKeyLock = "twitter_task_refresh_token";
    @Autowired
    private TwitterOAuth2Service twitterOAuth2Service;
    @Autowired
    private RedisLockUtil redisLockUtil;
    @Autowired
    private TwitterConfig twitterConfig;
    @Autowired
    private ConfigProperties configProperties;

    /**
     * 初始化10分钟后开始执行, 间隔30分钟执行一次
     * @throws InterruptedException
     */
    @Scheduled(
            initialDelay = 600000L,
            fixedRate = 1800000L
    )
    public void jobTask() throws InterruptedException {
        // 配置有设置激活
        if (configProperties.isEnableTwitterRefreshToken()) {
            log.info("starting schedule task for twitter_task_refresh_token ......");
            String requestId = LocalDateTime.now().format(dtf);
            // 获取锁
            Boolean lock = redisLockUtil.tryLock("twitter_task_refresh_token", requestId, 60L);
            if (null != lock && lock) {
                log.info("starting schedule task for twitter_task_refresh_token, get lock ......");
                boolean isContinue = true;
                int time = 2;

                while (isContinue && time > 0) {
                    try {
                        refreshToken();
                        isContinue = false;
                    } catch (Exception var9) {
                        log.error("schedule task for twitter_task_refresh_token error {} time", time, var9);
                    } finally {
                        --time;
                        // 休眠1s再执行
                        Thread.sleep(1000L);

                        // 已经执行成功, 或者是最后一次, 释放锁
                        if (!isContinue || time == 0) {
                            redisLockUtil.releaseLock("twitter_task_refresh_token", requestId);
                        }

                    }
                }
            } else {
                log.warn("redis get lock fail |{}|{}", "twitter_task_refresh_token", lock);
            }

            log.info("finish schedule task for twitter_task_refresh_token ......");
        }
    }

    private void refreshToken() throws InterruptedException, IOException {
        TwitterRefreshTokenRequestParam refreshTokenRequestParam = TwitterRefreshTokenRequestParam.builder()
                .oAuthClientId(twitterConfig.getOAuthClientID())
                .oAuthClientSecret(twitterConfig.getOAuthClientSecret())
                .build();
        OAuth2AccessToken oAuth2AccessToken = twitterOAuth2Service.refreshAccessToken(refreshTokenRequestParam, TwitterCredentialsFactory.getCredentials().getRefreshToken());
        TwitterCredentialsFactory.updateCredentials(oAuth2AccessToken);
        log.info("access token: {} refresh token: {}", oAuth2AccessToken.getAccessToken(), oAuth2AccessToken.getRefreshToken());
        Thread.sleep(2000L);
    }
}
