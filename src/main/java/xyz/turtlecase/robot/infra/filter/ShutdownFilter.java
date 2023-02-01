package xyz.turtlecase.robot.infra.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

/**
 * 服务关闭约束
 */
@Component
public class ShutdownFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(ShutdownFilter.class);
    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();
    @Value("${shutdown.whitelist:0:0:0:0:0:0:0:1}")
    private String[] shutdownIpWhitelist;

    public void destroy() {
    }

    public void doFilter(ServletRequest srequest, ServletResponse sresponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) srequest;
        if (!antPathMatcher.match(request.getRequestURI(), "/actuator/shutdown")) {
            filterChain.doFilter(srequest, sresponse);
        } else {
            String ip = this.getIpAddress(request);
            logger.info("访问shutdown的机器的原始IP：{}", ip);
            if (!this.isMatchWhiteList(ip)) {
                sresponse.setContentType("application/json");
                sresponse.setCharacterEncoding("UTF-8");
                PrintWriter writer = sresponse.getWriter();
                writer.write("{\"code\":401}");
                writer.flush();
                writer.close();
            } else {
                filterChain.doFilter(srequest, sresponse);
            }
        }
    }

    public void init(FilterConfig arg0) throws ServletException {
        logger.info("shutdown filter is init.....");
    }

    private boolean isMatchWhiteList(String ip) {
        List<String> list = Arrays.asList(this.shutdownIpWhitelist);
        return list.contains(ip) ? true : list.stream().anyMatch((data) -> {
            return ip.startsWith(data);
        });
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }
}
