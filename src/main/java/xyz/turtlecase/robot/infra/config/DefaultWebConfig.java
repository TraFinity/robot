package xyz.turtlecase.robot.infra.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import xyz.turtlecase.robot.infra.web.ErrorView;

/**
 * web配置
 */
@Configuration
public class DefaultWebConfig extends WebMvcConfigurationSupport {
    @Bean({"error"})
    public ErrorView error() {
        return new ErrorView();
    }

    /**
     * 定义时间格式转换器
     *
     * @return
     */
    @Bean
    public MappingJackson2HttpMessageConverter jackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));
        converter.setObjectMapper(mapper);
        return converter;
    }

    /**
     * 添加转换器, 配置springmvc返回数据时输出数据的格式, 此处只配置了时间的输出格式
     *
     * @param converters a list to add message converters to (initially an empty list)
     */
    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(jackson2HttpMessageConverter());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        // 注册spring data jpa pageable参数分解器
        argumentResolvers.add(new PageableHandlerMethodArgumentResolver());
    }
}
