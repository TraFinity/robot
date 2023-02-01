package xyz.turtlecase.robot.business.twitter.biz.mapper;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Table;

@Data
@EqualsAndHashCode
@Table(name = "twitter_list_user")
public class TwitterListUserPo {
    private String listId;
    private String userId;
    private String userName;
}
