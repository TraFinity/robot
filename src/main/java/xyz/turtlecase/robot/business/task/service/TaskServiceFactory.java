package xyz.turtlecase.robot.business.task.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import xyz.turtlecase.robot.business.task.dto.TaskType;

/**
 * task任务工厂
 */
@Component
public class TaskServiceFactory implements ApplicationContextAware {
    private static Map<TaskType, TaskService> taskServiceMap;

    public static TaskService getTaskService(TaskType taskType) {
        return taskServiceMap.get(taskType);
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, TaskService> map = applicationContext.getBeansOfType(TaskService.class);
        taskServiceMap = new HashMap();
        map.forEach((key, value) -> taskServiceMap.put(value.getTaskType(), value));
    }
}
