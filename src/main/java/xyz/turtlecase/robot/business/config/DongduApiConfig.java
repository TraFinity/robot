package xyz.turtlecase.robot.business.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.turtlecase.robot.business.dongdu.DongduApi;
import xyz.turtlecase.robot.infra.config.ConfigProperties;

/**
 * 机器人接口注册
 */
@Configuration
public class DongduApiConfig {
    @Bean
    public DongduApi dongduApi(ConfigProperties configProperties) {
        return new DongduApi(configProperties.getDongduUrl());
    }
}
