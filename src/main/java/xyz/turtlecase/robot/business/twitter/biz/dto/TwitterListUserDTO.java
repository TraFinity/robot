package xyz.turtlecase.robot.business.twitter.biz.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class TwitterListUserDTO {
    private String listId;
    private String userId;
    private String userName;
}
