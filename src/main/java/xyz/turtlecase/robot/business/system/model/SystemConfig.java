package xyz.turtlecase.robot.business.system.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import xyz.turtlecase.robot.infra.model.BaseModel;

/**
 * 字典配置基类
 */
@Data
@EqualsAndHashCode
public class SystemConfig extends BaseModel {
    /**
     * 一级类型
     */
    private String type;
    /**
     * 键
     */
    private String dictKey;
    /**
     * 值
     */
    private String dictValue;
    /**
     * 序号
     */
    private Integer sn;
    /**
     * 备注
     */
    private String remark;
    /**
     * 状态
     */
    private Integer status;
}
