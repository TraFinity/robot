package xyz.turtlecase.robot.infra.utils.http.okhttp;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.time.StopWatch;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OKHttp日志拦截器
 */
@Slf4j
class LoggingInterceptor implements Interceptor {

    @NotNull
    @Override
    public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
        StopWatch sw = new StopWatch();
        sw.start();
        Request request = chain.request();
        log.info("okHttp request:: {} url: {}\n body:{}", request.method(), request.url());
        Response response = chain.proceed(request);
        sw.split();
        log.info("okHttp:: response: in {}ms, status code: {} url: {}\nheaders: {}", sw.getSplitTime(), response.code(), request.url(), response.headers());
        return response;
    }
}
