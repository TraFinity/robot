package xyz.turtlecase.robot.business.twitter.biz.mapper;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Table;

@Data
@EqualsAndHashCode
@Table(name = "twitter_list")
public class TwitterListPo {
    private String id;
    private String slug;
    private String name;
    /**
     * twitter账户人的ID
     */
    private String userId;
}
