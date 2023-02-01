package xyz.turtlecase.robot.business.task.dto;

/**
 * 任务类型
 */
public enum TaskType {
    /**
     * follow一个账号
     */
    TWITTER_FOLLOW_MEMBER,
    /**
     * like一个账号
     */
    TWITTER_LIKE_MEMBER,
    /**
     * follow多个账号
     */
    TWITTER_FOLLOW_MEMBERS,
    /**
     * 回复一个tweet
     */
    TWITTER_REPLY_TWEET,
    /**
     * 转发tweet
     */
    TWITTER_RE_TWEET,
    /**
     * 引用tweet
     */
    TWITTER_QUOTE_TWEET,
    /**
     * like tweet
     */
    TWITTER_LIKE_TWEET;
}
