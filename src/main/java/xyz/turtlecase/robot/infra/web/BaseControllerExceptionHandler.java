package xyz.turtlecase.robot.infra.web;

import java.util.Collection;
import java.util.Iterator;
import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import xyz.turtlecase.robot.infra.exception.BaseException;
import xyz.turtlecase.robot.infra.utils.CollectionUtils;
import xyz.turtlecase.robot.infra.utils.UniqueIdGenerator;

/**
 * web异常处理
 */
@ControllerAdvice
public class BaseControllerExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(BaseControllerExceptionHandler.class);
    private static final String DEFAULT_ERROR_MESSAGE = "Service error, `please contact system operator`";
    private static final String ERROR_MESSAGE_KEY = "biz.errorMessage";
    @Resource
    Environment environment;

    /**
     * 拦截业务异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler({BaseException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestResult<String> internalError(BaseException ex) {

        logger.error("BaseControllerExceptionHandler internalError ", ex);

        return RestResultGenerator.genErrorResult(ex.getMessage());

    }

    /**
     * 拦截未知的运行时异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler({RuntimeException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public RestResult<String> internalError(RuntimeException ex) {

        logger.error("BaseControllerExceptionHandler internalError ", ex);

        // 如果是校验类的异常
        if (ex instanceof ConstraintViolationException) {
            StringBuilder msg = new StringBuilder("");
            ConstraintViolationException e = (ConstraintViolationException) ex;
            if (!CollectionUtils.isEmpty(e.getConstraintViolations())) {
                for (ConstraintViolation constraintViolation : ((ConstraintViolationException) ex).getConstraintViolations()) {
                    msg.append(constraintViolation.getMessage()).append("; ");
                }

                return RestResultGenerator.genErrorResult(msg.toString());
            }
            return RestResultGenerator.genErrorResult(StringUtils.trimToEmpty(StringUtils.substringAfter(ex.getMessage(), ":")));

        }

        // sql 重复键异常
        if (ex instanceof org.springframework.dao.DuplicateKeyException) {
            return RestResultGenerator.genErrorResult("duplicate entry");

        }
        return RestResultGenerator.genErrorResult(DEFAULT_ERROR_MESSAGE);

    }

    /**
     * valid校验产生的MethodArgumentNotValidException不能直接捕获, 因为父类已经捕获了, 因此重写父类的这个方法
     *
     * @param ex      the exception
     * @param headers the headers to be written to the response
     * @param status  the selected response status
     * @param request the current request
     * @return
     */
    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                               HttpStatus status, WebRequest request) {

        logger.error("BaseControllerExceptionHandler handleMethodArgumentNotValid ", (Throwable) ex);

        BindingResult bindingResult = ex.getBindingResult();

        RestResult restResult = RestResultGenerator.genErrorResult(bindingResult.getFieldError().getDefaultMessage());

        return new ResponseEntity(restResult, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    /**
     * 重写原因如上
     *
     * @param ex      the exception
     * @param headers the headers to be written to the response
     * @param status  the selected response status
     * @param request the current request
     * @return
     */
    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers,
                                                         HttpStatus status, WebRequest request) {

        logger.error("BaseControllerExceptionHandler handleBindException ", ex);
        BindingResult bindingResult = ex.getBindingResult();

        RestResult restResult = RestResultGenerator.genErrorResult(bindingResult.getFieldError().getDefaultMessage());

        return new ResponseEntity(restResult, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers, HttpStatus status,
                                                                          WebRequest request) {

        logger.error("BaseControllerExceptionHandler handleMissingServletRequestParameter ", (Throwable) ex);
        RestResult restResult = RestResultGenerator.genErrorResult(ex.getMessage());

        return new ResponseEntity(restResult, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    /**
     * 拦截未知的运行时异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler({Exception.class})
    @ResponseBody
    public RestResult<String> internalError(Exception ex) {

        logger.error("BaseControllerExceptionHandler internalError ", ex);

        return RestResultGenerator.genErrorResult(ex.getMessage());

    }

    private String getErrorMessage() {

        String errorMessage = this.environment.getProperty(ERROR_MESSAGE_KEY);

        if (StringUtils.isBlank(errorMessage)) {

            return DEFAULT_ERROR_MESSAGE;

        }

        return errorMessage;

    }
}
