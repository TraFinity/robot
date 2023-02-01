package xyz.turtlecase.robot.business.twitter.api.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class TwitterBooleanData {
    private Boolean deleted;

    @JSONField(name = "is_member")
    private Boolean is_member;

}
