package xyz.turtlecase.robot;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;
import xyz.turtlecase.robot.infra.utils.Env;
import xyz.turtlecase.robot.infra.utils.SpringBeanUtils;

/**
 * 启动类
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableCaching
@MapperScan({"xyz.turtlecase.robot.business.nftsync.mapper",
        "xyz.turtlecase.robot.business.twitter.biz.mapper",
        "xyz.turtlecase.robot.business.task.mapper",
        "xyz.turtlecase.robot.business.system.mapper"})
@EnableScheduling
public class RobotApplication {
    public static void main(String[] args) throws IOException {
        System.setProperty("jdk.tls.useExtendedMasterSecret", "false");
        Env.loadProperties();
        System.out.println("********** This is application env is prd :" + Env.isPrdEnv() + " ********** ");
        ConfigurableApplicationContext applicationContext = SpringApplication.run(RobotApplication.class, args);
        SpringBeanUtils.setApplicationContext(applicationContext);
    }
}
