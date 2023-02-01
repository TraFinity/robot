package xyz.turtlecase.robot.business.system.service;

import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.turtlecase.robot.business.system.dto.SystemConfigDTO;
import xyz.turtlecase.robot.business.system.mapper.SystemConfigMapper;
import xyz.turtlecase.robot.business.system.mapper.SystemConfigPo;
import xyz.turtlecase.robot.infra.model.BaseStatusEnum;
import xyz.turtlecase.robot.infra.utils.BeanCopy;

@Service
public class SystemConfigServiceImpl implements SystemConfigService {
    @Autowired
    private SystemConfigMapper systemConfigMapper;

    /**
     * 新增
     *
     * @param systemConfigDTO
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void create(SystemConfigDTO systemConfigDTO) {
        SystemConfigPo systemConfigPo = BeanCopy.copyBean(systemConfigDTO, SystemConfigPo.class);
        Date date = new Date();
        systemConfigPo.setCreateTime(date);
        systemConfigPo.setUpdateTime(date);
        if (systemConfigPo.getStatus() == null) {
            systemConfigPo.setStatus(BaseStatusEnum.ENABLE.getValue());
        }

        systemConfigMapper.insert(systemConfigPo);
    }

    /**
     * 更新
     *
     * @param systemConfigDTO
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void update(SystemConfigDTO systemConfigDTO) {
        SystemConfigPo systemConfigPo = BeanCopy.copyBean(systemConfigDTO, SystemConfigPo.class);
        Date date = new Date();
        systemConfigPo.setUpdateTime(date);
        systemConfigMapper.updateByPrimaryKey(systemConfigPo);
    }

    /**
     * 禁用
     *
     * @param pkId
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void disable(@NotNull Long pkId) {
        SystemConfigPo systemConfigPo = new SystemConfigPo();
        systemConfigPo.setId(pkId);
        systemConfigPo.setUpdateTime(new Date());
        if (systemConfigPo.getStatus() == null) {
            systemConfigPo.setStatus(BaseStatusEnum.DISABLE.getValue());
        }

        systemConfigMapper.updateByPrimaryKeySelective(systemConfigPo);
    }

    /**
     * 加载记录
     *
     * @param systemConfigDTO
     * @return
     */
    public SystemConfigDTO load(SystemConfigDTO systemConfigDTO) {
        SystemConfigPo systemConfigPo = systemConfigMapper.selectOne(BeanCopy.copyBean(systemConfigDTO, SystemConfigPo.class));
        return BeanCopy.copyBean(systemConfigPo, SystemConfigDTO.class);
    }

    /**
     * 查询
     *
     * @param systemConfigDTO
     * @return
     */
    public List<SystemConfigDTO> query(SystemConfigDTO systemConfigDTO) {
        List<SystemConfigPo> list = systemConfigMapper.select(BeanCopy.copyBean(systemConfigDTO, SystemConfigPo.class));
        return BeanCopy.copyBeans(list, SystemConfigDTO.class);
    }
}
