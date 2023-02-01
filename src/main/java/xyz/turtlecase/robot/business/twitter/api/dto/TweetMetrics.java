package xyz.turtlecase.robot.business.twitter.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class TweetMetrics {
    private Long retweet_count;
    private Long reply_count;
    private Long like_count;
    private Long quote_count;

}
