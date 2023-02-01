package xyz.turtlecase.robot.business.twitter.biz.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class TwitterListDTO {
    private String id;
    private String slug;
    private String name;
    private String url;

    public String getUrl() {
        return "https://twitter.com/i/lists/" + this.id + "/members";
    }

}
