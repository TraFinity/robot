package xyz.turtlecase.robot.business.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.turtlecase.robot.business.twitter.api.TwitterClient;
import xyz.turtlecase.robot.business.twitter.api.TwitterConfig;
import xyz.turtlecase.robot.infra.service.RateLimitService;

/**
 * twitter bean 注册
 */
@Configuration
public class TwitterBeanConfig {
    @Autowired
    private TwitterConfig twitterConfig;
    @Autowired
    private RateLimitService rateLimitService;

    /**
     * 初始化twitter客户端
     *
     * @return
     */
    @Bean
    public TwitterClient createTwitterClient() {
        return new TwitterClient(this.twitterConfig, this.rateLimitService);
    }
}
