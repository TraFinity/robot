package xyz.turtlecase.robot.business.twitter.biz.mapper;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Table;

@Data
@EqualsAndHashCode
@Table(name = "twitter_user")
public class TwitterUserPo {
    private String id;
    private String userName;
}
