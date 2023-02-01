package xyz.turtlecase.robot.infra.web;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.View;

/**
 * 异常页面, 未使用, 由前端进行跳转
 */
public class ErrorView implements View {
    public String getContentType() {
        return "text/html";
    }

    public void render(Map<String, ?> map, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        httpServletRequest.getRequestDispatcher("/global/error").forward(httpServletRequest, httpServletResponse);
    }
}
