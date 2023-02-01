package xyz.turtlecase.robot.infra.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import xyz.turtlecase.robot.infra.exception.BaseException;

public final class BeanCopy {
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static final <T> T copyBean(Object source, Class<T> clazz) {
        // 源为空, 直接返回空
        if (source == null) {
            return null;
        }
        T target = null;
        try {
            target = (T) Class.forName(clazz.getName()).newInstance();
        } catch (Exception e) {
            throw new BaseException(e);
        }
        BeanUtils.copyProperties(source, target);
        return target;
    }

    public static <T> List<T> copyBeans(List sources, Class<T> clazz) {

        if (CollectionUtils.isEmpty(sources)) {
            return new ArrayList<>();
        }
        List<T> list = new ArrayList<>(sources.size());
        for (Object source : sources) {
            list.add(copyBean(source, clazz));
        }
        return list;
    }
}
