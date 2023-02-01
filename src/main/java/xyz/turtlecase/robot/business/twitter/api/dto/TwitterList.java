package xyz.turtlecase.robot.business.twitter.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * twitter list实体
 */
@Data
@EqualsAndHashCode
public class TwitterList {
    private String id;
    private String name;
    private String created_at;
    private String follower_count;
    private String member_count;
    private String description;
    private String owner_id;
    private String url;

    public String getUrl() {
        return "https://twitter.com/i/lists/" + this.id + "/members";
    }

}
