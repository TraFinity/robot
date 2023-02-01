package xyz.turtlecase.robot.business.system.service;

import java.util.List;
import javax.validation.constraints.NotNull;

import xyz.turtlecase.robot.business.system.dto.SystemConfigDTO;

/**
 * 字典服务接口
 */
public interface SystemConfigService {
    /**
     * 创建系统配置对象
     *
     * @param systemConfigDTO
     */
    void create(@NotNull SystemConfigDTO systemConfigDTO);

    /**
     * 更新
     *
     * @param systemConfigDTO
     */
    void update(@NotNull SystemConfigDTO systemConfigDTO);

    /**
     * 禁用
     *
     * @param pkId
     */
    void disable(@NotNull Long pkId);

    /**
     * 加载
     *
     * @param systemConfigDTO
     * @return
     */
    SystemConfigDTO load(@NotNull SystemConfigDTO systemConfigDTO);

    /**
     * 查询
     *
     * @param systemConfigDTO
     * @return
     */
    List<SystemConfigDTO> query(@NotNull SystemConfigDTO systemConfigDTO);
}
