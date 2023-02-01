package xyz.turtlecase.robot.business.twitter.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * twitter api调用返回的错误日志
 */
@Data
@EqualsAndHashCode
public class TwitterError {
    /**
     * 查询条件
     */
    private String value;
    /**
     * 错误明细
     */
    private String detail;

    /**
     * 异常标题
     */
    private String title;

    /**
     * 资源类型
     */
    private String resource_type;

    /**
     * 参数名
     */
    private String parameter;

    /**
     * 资源名
     */
    private String resource_id;

    /**
     * 异常类型
     */
    private String type;
}
