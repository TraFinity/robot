package xyz.turtlecase.robot.infra.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import xyz.turtlecase.robot.infra.exception.BaseException;

@Slf4j
public final class Env {
    private static Boolean isProEnv = null;

    public static void loadProperties() throws IOException {
        Properties properties = new Properties();
        String path = CommonUtil.getEnv("config_path");
        if (StringUtils.isBlank(path)) {
            log.info("begin to load config.properties, put into jvm properties... ");
            ClassPathResource classPathResource = new ClassPathResource("config.properties");
            properties.load(classPathResource.getInputStream());
        } else {
            log.info("begin to load {}, put into jvm properties... ", path);
            properties.load(new FileInputStream(new File(path)));
        }

        if (properties.isEmpty()) {
            throw new BaseException("please set config_path in Env or jvm properties, please see the config.properties sample");
        } else {
            // 临时方案, 塞到jvm properties里面, 供logback, application.yaml业务配置初始化使用
            for (String key : properties.stringPropertyNames()) {
                if (System.getProperties().get(key) == null) {
                    System.getProperties().put(key, properties.get(key));
                }
            }
        }
    }

    /**
     * 是否线上环境
     *
     * @return
     */
    public static boolean isPrdEnv() {
        if (isProEnv == null) {
            if (StringUtils.equalsIgnoreCase("release", CommonUtil.getEnv("active_env"))) {
                isProEnv = Boolean.TRUE;
            } else {
                isProEnv = Boolean.FALSE;
            }
        }

        return isProEnv;
    }

    /**
     * 是否测试环境
     *
     * @return
     */
    public static boolean isTestEnv() {
        return StringUtils.equalsIgnoreCase("test", CommonUtil.getEnv("active_env"));
    }

    /**
     * 获取运行环境参数
     *
     * @return
     */
    public static String getEnv() {
        return CommonUtil.getEnv("active_env");
    }
}
