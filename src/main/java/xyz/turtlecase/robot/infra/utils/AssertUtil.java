package xyz.turtlecase.robot.infra.utils;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import xyz.turtlecase.robot.infra.exception.BaseException;

public final class AssertUtil {
    /**
     * 参考guava PreConditions, 封装异常
     *
     * @param obj
     * @param errorMessage
     */
    public static final void checkNotNull(Object obj, String errorMessage) {

        if (obj == null) {
            throw new BaseException(errorMessage);
        }
        // 对字符串特殊处理
        if (obj instanceof String &&
                StringUtils.isBlank((String) obj)) {
            throw new BaseException(errorMessage);
        }
    }

    public static final void checkCollectionNotEmpty(Collection collection, String errorMessage) {
        if (collection == null || collection.isEmpty()) {
            throw new BaseException(errorMessage);
        }
    }

    public static final void checkStringNotEmpty(CharSequence charSequence, String errorMessage) {
        if (charSequence == null || charSequence.length() == 0) {
            throw new BaseException(errorMessage);
        }
    }

    public static final void checkArgument(boolean expression, String errorMessage) {
        if (expression) {
            throw new BaseException(errorMessage);
        }
    }

    /**
     * 判断是否正数
     *
     * @param number
     * @param errorMessage
     */
    public static final void checkPositiveNumber(Number number, String errorMessage) {

        if (number == null) {
            throw new BaseException(errorMessage);
        }

        if (number instanceof Integer && number.intValue() < 1) {
            throw new BaseException(errorMessage);
        }

        if (number instanceof Long && number.longValue() < 1L) {
            throw new BaseException(errorMessage);
        }
    }
}
