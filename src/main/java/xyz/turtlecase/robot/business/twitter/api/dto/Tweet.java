package xyz.turtlecase.robot.business.twitter.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * tweet对象
 */
@Data
@EqualsAndHashCode
public class Tweet {
    private String id;
    private String text;
    private String author_id;
    private TweetMetrics public_metrics;
}
