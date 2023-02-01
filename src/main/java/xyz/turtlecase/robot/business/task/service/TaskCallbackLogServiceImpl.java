package xyz.turtlecase.robot.business.task.service;

import java.util.Date;
import javax.validation.constraints.NotNull;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.turtlecase.robot.business.task.dto.TaskCallbackLogDTO;
import xyz.turtlecase.robot.business.task.mapper.TaskCallbackLogMapper;
import xyz.turtlecase.robot.business.task.mapper.TaskCallbackLogPo;
import xyz.turtlecase.robot.infra.utils.BeanCopy;

/**
 * 回调日志记录服务
 */
@Slf4j
@Service
public class TaskCallbackLogServiceImpl implements TaskCallbackLogService {
    @Autowired
    private TaskCallbackLogMapper taskCallbackLogMapper;

    /**
     * 根据task id获取数据
     *
     * @param taskId
     * @return
     */
    @Override
    public TaskCallbackLogDTO getByTaskId(@NotNull Integer taskId) {
        TaskCallbackLogPo query = new TaskCallbackLogPo();
        query.setTaskId(taskId);
        return BeanCopy.copyBean(taskCallbackLogMapper.selectOne(query), TaskCallbackLogDTO.class);
    }

    /**
     * 创建
     *
     * @param taskCallbackLogDTO
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void create(TaskCallbackLogDTO taskCallbackLogDTO) {
        TaskCallbackLogPo taskCallbackLogPo = BeanCopy.copyBean(taskCallbackLogDTO, TaskCallbackLogPo.class);
        Date date = new Date();
        taskCallbackLogPo.setCreateTime(date);
        taskCallbackLogPo.setUpdateTime(date);
        taskCallbackLogMapper.insert(taskCallbackLogPo);
    }

    /**
     * 更新
     *
     * @param taskCallbackLogDTO
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void update(TaskCallbackLogDTO taskCallbackLogDTO) {
        TaskCallbackLogPo taskCallbackLogPo = BeanCopy.copyBean(taskCallbackLogDTO, TaskCallbackLogPo.class);
        Date date = new Date();
        taskCallbackLogPo.setUpdateTime(date);
        taskCallbackLogMapper.updateByPrimaryKey(taskCallbackLogPo);
    }
}
