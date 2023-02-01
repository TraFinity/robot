package xyz.turtlecase.robot.infra.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.turtlecase.robot.infra.exception.BaseException;

@Slf4j
public class JsonUtils {
    /**
     * 定义jackson对象
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 将对象转换为json字符串
     *
     * @param data
     * @return
     */
    public static String objectToJson(Object data) {
        try {
            String string = MAPPER.writeValueAsString(data);
            return string;

        } catch (JsonProcessingException e) {
            log.error("objectToJson ", (Throwable) e);
            throw new BaseException("param error");
        }
    }

    /**
     * 将json结果集转换为对象
     *
     * @param jsonData
     * @param beanType
     * @param <T>
     * @return
     */
    public static <T> T jsonToPojo(String jsonData, Class<T> beanType) {
        try {

            T t = (T) MAPPER.readValue(jsonData, beanType);
            return t;

        } catch (Exception e) {
            log.error("objectToJson ", e);
            throw new BaseException("param error");
        }
    }

    /**
     * 将json数据转换成pojo对象list
     *
     * @param jsonData
     * @param beanType
     * @param <T>
     * @return
     */
    public static <T> List<T> jsonToList(String jsonData, Class<T> beanType) {

        JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, beanType);
        try {
            List<T> list = MAPPER.readValue(jsonData, javaType);
            return list;
        } catch (Exception e) {
            log.error("objectToJson ", e);
            throw new BaseException("param error");
        }
    }
}
