package xyz.turtlecase.robot.infra.exception;

/**
 * 限流异常类
 */
public class RateLimitingException extends RuntimeException {
    /**
     * 异常消息
     */
    private String errorMessage = "Too Many Requests";
    private String requestKey;

    /**
     * 异常码
     */
    private String errorCode = "429";

    public RateLimitingException() {
    }

    public RateLimitingException(String requestKey) {
        super("Too Many Requests");
        this.requestKey = requestKey;
    }
}
