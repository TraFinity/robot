package xyz.turtlecase.robot.business.twitter.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
public class TwitterResponse<T> {
    /**
     * 数据类型
     */
    private T data;

    /**
     * 错误内容
     */
    private List<TwitterError> errors;

    /**
     * 分布
     */
    private PaginationMeta meta;
}
