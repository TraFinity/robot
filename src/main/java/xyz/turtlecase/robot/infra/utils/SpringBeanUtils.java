package xyz.turtlecase.robot.infra.utils;

import org.springframework.context.ApplicationContext;

public class SpringBeanUtils {
    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext) {

        SpringBeanUtils.applicationContext = applicationContext;
    }

    public static Object getBean(String name) {

        return applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> T) {

        return (T) applicationContext.getBean(T);
    }
}
