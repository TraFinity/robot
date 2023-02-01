package xyz.turtlecase.robot.business.twitter.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * twitter http 异常状态时的消息
 */
@Data
@EqualsAndHashCode
public class TwitterHttpError {
    private String title;
    private String type;
    private Integer status;
    private String detail;

}
