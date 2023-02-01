package xyz.turtlecase.robot.infra.exception;

/**
 * 异常基类
 */
public class BaseException extends RuntimeException {
    /**
     * 异常消息
     */
    private String errorMessage;

    /**
     * 异常码
     */
    private String errorCode;

    public BaseException(String errorMessage) {
        super(errorMessage);
        this.errorCode = "500";
        this.errorMessage = errorMessage;
    }

    public BaseException(String errorMessage, String errorCode) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public BaseException(Throwable cause) {
        this.errorCode = "500";
        this.errorMessage = cause.getMessage();
    }
}
