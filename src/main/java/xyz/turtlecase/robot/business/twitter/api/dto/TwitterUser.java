package xyz.turtlecase.robot.business.twitter.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class TwitterUser {
    private String id;
    private String name;
    private String username;
}
