package xyz.turtlecase.robot.infra.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.turtlecase.robot.infra.config.ConfigProperties;
import xyz.turtlecase.robot.infra.exception.BaseException;

/**
 * FreeMarker接口实现
 */
@Slf4j
@Service
public class FreeMarkerServiceImpl implements FreeMarkerService {
    @Autowired
    private Configuration configuration;
    @Autowired
    private ConfigProperties configProperties;

    /**
     * 读取文件的模板生成内容
     *
     * @param templateFileName 文件名
     * @param dataModel        数据
     * @return
     */
    public String processTemplate(String templateFileName, Object dataModel) {
        try {

            String templateValue = FileUtils.readFileToString(
                    new File(configProperties.getTemplatePath() + "/" + templateFileName), StandardCharsets.UTF_8);
            return processTemplate(templateFileName, templateValue, dataModel);

        } catch (IOException e) {
            log.error("FreeMarker read  template file " + templateFileName, e);
            throw new BaseException("internal error while parse data");
        }
    }

    /**
     * 根据模板名生成内容
     *
     * @param templateName  模板名
     * @param templateValue 模板值
     * @param dataModel     数据
     * @return
     */
    public String processTemplate(String templateName, String templateValue, Object dataModel) {
        StringWriter stringWriter = new StringWriter();
        Template template = null;
        try {
            template = new Template(templateName, templateValue, configuration);
            template.process(dataModel, stringWriter);
            return stringWriter.toString();
        } catch (IOException | freemarker.template.TemplateException e) {
            log.error("FreeMarker process template error", e);
            throw new BaseException("internal error while parse data");
        }
    }
}
