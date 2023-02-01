package xyz.turtlecase.robot.business.dongdu;

import java.io.IOException;
import javax.validation.constraints.NotNull;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import xyz.turtlecase.robot.infra.utils.http.okhttp.OkHttpResponse;
import xyz.turtlecase.robot.infra.utils.http.okhttp.OkHttpUtils;

/**
 * 回调webapp api
 * todo 后续最好使用服务注册
 */
@Slf4j
@Validated
public class DongduApi {
    private final DongduErrorHandler dongduErrorHandler = new DongduErrorHandler();
    /**
     * webApp 根url
     */
    private String baseUrl;

    public DongduApi(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * 回调任务进行结算url
     *
     * @param taskId
     * @return
     */
    public String getCallbackUrl(@NotNull Integer taskId) {
        StringBuilder url = new StringBuilder(this.baseUrl);
        url.append("/api/v2/addons/callback/taskCheck/").append(taskId);
        return url.toString();
    }

    /**
     * 回调webApp 进行任务结算
     *
     * @param taskId
     * @throws IOException
     */
    public void taskCallback(@NotNull Integer taskId) throws IOException {
        String url = getCallbackUrl(taskId);
        log.info("call dong du {}, state:{} code:{}", url, taskId);
        OkHttpResponse okHttpResponse = OkHttpUtils.builder().url(url)
                .addHeader("content-type", "application/json; charset=utf-8")
                .post(Boolean.TRUE)
                .sync(this.dongduErrorHandler);

        assert okHttpResponse != null;

        assert okHttpResponse.getSuccess();

    }
}
