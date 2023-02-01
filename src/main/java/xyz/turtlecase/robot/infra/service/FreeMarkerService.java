package xyz.turtlecase.robot.infra.service;

/**
 * freeMarker接口
 */
public interface FreeMarkerService {
    /**
     * 根据模板名解析
     *
     * @param templateName  模板名
     * @param templateValue 模板值
     * @param dataModel     数据
     * @return
     */
    String processTemplate(String templateName, String templateValue, Object dataModel);

    /**
     * 读取模板目录中的文件, 生成内容
     *
     * @param templateFileName 文件名
     * @param dataModel        数据
     * @return
     */
    String processTemplate(String templateFileName, Object dataModel);
}
