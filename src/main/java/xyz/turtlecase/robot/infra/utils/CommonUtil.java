package xyz.turtlecase.robot.infra.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * 通用工具类, 放杂七杂八的内容
 */
public final class CommonUtil {
    /**
     * 获取启动参数值
     *
     * @param key
     * @return
     */
    public static String getEnv(String key) {
        String value = System.getProperty(key);
        return StringUtils.isNotBlank(value) ? value : (String) System.getenv().get(key);
    }

    /**
     * 获取文件后缀名
     *
     * @param filePath
     * @return
     */
    public static String getFileType(String filePath) {
        String fileType = StringUtils.substringAfterLast(filePath, ".");
        return StringUtils.isBlank(fileType) ? "" : "." + fileType;
    }
}
