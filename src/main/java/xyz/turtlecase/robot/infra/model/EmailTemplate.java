package xyz.turtlecase.robot.infra.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import xyz.turtlecase.robot.infra.service.FreeMarkerService;
import xyz.turtlecase.robot.infra.utils.SpringBeanUtils;

@Data
public class EmailTemplate {
    private String from;
    /**
     * 接收人
     */
    @NotNull
    @Email
    private String to;
    @NotBlank
    private String subject;
    private String content;
    private String templateFileName;
    private Object dataModel;

    public String getContent() {

        if (StringUtils.isNotBlank(this.content)) {
            return this.content;
        }

        if (StringUtils.isNotBlank(this.templateFileName)) {
            FreeMarkerService freeMarkerService = (FreeMarkerService) SpringBeanUtils.getBean(FreeMarkerService.class);
            this.content = freeMarkerService.processTemplate(this.templateFileName, this.dataModel);
        }
        return this.content;
    }
}
