package xyz.turtlecase.robot.mq;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class TwitterListMemberVO {
    private String listName;
    private String twitterUserName;
}
