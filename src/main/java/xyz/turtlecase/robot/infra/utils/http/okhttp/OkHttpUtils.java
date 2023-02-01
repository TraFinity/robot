package xyz.turtlecase.robot.infra.utils.http.okhttp;

import com.alibaba.fastjson2.JSON;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.turtlecase.robot.infra.utils.CollectionUtils;

@Slf4j
public class OkHttpUtils {
    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");
    private static volatile OkHttpClient okHttpClient = null;
    private static volatile Semaphore semaphore = null;
    private Map<String, String> headerMap;
    private Map<String, Object> paramMap;
    private String url;
    private Request.Builder request;

    private OkHttpUtils() {
        okHttpClient = OkHttpClientInstance.getOkHttpClient();
    }

    /**
     * 用于异步请示时, 控制访问线程数, 返回结果
     *
     * @return
     */
    private static Semaphore getSemaphoreInstance() {
        // 只允许1个线程访问
        synchronized (OkHttpUtils.class) {
            if (semaphore == null) {
                semaphore = new Semaphore(1);
            }
        }

        return semaphore;
    }

    /**
     * 创建实例
     *
     * @return
     */
    public static OkHttpUtils builder() {
        return new OkHttpUtils();
    }

    /**
     * 添加url
     *
     * @param url
     * @return
     */
    public OkHttpUtils url(String url) {
        this.url = url;
        return this;
    }

    /**
     * 添加参数
     *
     * @param key   参数名
     * @param value 值
     * @return
     */
    public OkHttpUtils addParam(String key, Object value) {
        if (this.paramMap == null) {
            this.paramMap = new LinkedHashMap(16);
        }

        this.paramMap.put(key, value);
        return this;
    }

    /**
     * 添加Header
     *
     * @param key   参数名
     * @param value 值
     * @return
     */
    public OkHttpUtils addHeader(String key, String value) {
        if (this.headerMap == null) {
            this.headerMap = new LinkedHashMap(16);
        }

        this.headerMap.put(key, value);
        return this;
    }

    /**
     * 初始化get方法
     *
     * @return
     */
    public OkHttpUtils get() {
        this.request = new Request.Builder().get();
        StringBuilder urlBuilder = new StringBuilder(this.url);
        if (this.paramMap != null && !CollectionUtils.isEmpty(this.paramMap)) {
            urlBuilder.append("?");

            try {
                for (Map.Entry<String, Object> entry : this.paramMap.entrySet()) {
                    urlBuilder.append(URLEncoder.encode(entry.getKey(), "utf-8"))
                            .append("=")
                            .append(entry.getValue() != null ? URLEncoder.encode(entry.getValue().toString(), "utf-8") : "")
                            .append("&");

                }
            } catch (Exception e) {
                log.error("okhttp get", e);
            }

            urlBuilder.deleteCharAt(urlBuilder.length() - 1);
        }

        this.request.url(urlBuilder.toString());
        return this;
    }

    /**
     * 初始化post请示
     *
     * @param isJsonPost true则使用json提交, false则表单提交
     * @return
     */
    public OkHttpUtils post(boolean isJsonPost) {
        Object requestBody;
        if (isJsonPost) {
            String json = "";
            if (this.paramMap != null) {
                json = JSON.toJSONString(this.paramMap);
            }

            requestBody = RequestBody.create(json, JSON_MEDIA_TYPE);
        } else {
            FormBody.Builder formBody = new FormBody.Builder();
            if (this.paramMap != null) {
                // todo 低效, 待改进
                for (String key : paramMap.keySet()) {
                    formBody.add(key, this.paramMap.get(key).toString());

                }
            }

            requestBody = formBody.build();
        }

        this.request = new Request.Builder().post((RequestBody) requestBody).url(this.url);
        return this;
    }

    public OkHttpUtils delete() {
        this.request = new Request.Builder().delete().url(this.url);
        return this;
    }

    /**
     * 同步请示
     *
     * @param httpErrorHandler
     * @return
     * @throws IOException
     */
    public OkHttpResponse sync(OkHttpErrorHandler httpErrorHandler) throws IOException {
        this.setHeader(this.request);
        Response response = okHttpClient.newCall(this.request.build()).execute();

        assert response.body() != null;

        String body = response.body().string();
        int code = response.code();
        Boolean success = response.isSuccessful();
        response.body().close();

        OkHttpResponse okHttpResponse = OkHttpResponse.builder().success(success)
                .httpStatus(code)
                .body(body)
                .build();
        if (!Boolean.TRUE.equals(okHttpResponse.getSuccess())) {
            log.info("okHttp sync response:: status:{} url: {} \nbody: {}", response.code(), this.request.build().url().url(), body);
            httpErrorHandler.errorHandler(okHttpResponse);
        }

        return okHttpResponse;
    }

    private void setHeader(Request.Builder request) {
        if (this.headerMap != null && !this.headerMap.isEmpty()) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
        }

    }

    /**
     * 异步请示, 有返回值
     *
     * @return
     */
    public String async() {
        final StringBuilder buffer = new StringBuilder("");
        this.setHeader(this.request);
        okHttpClient.newCall(this.request.build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                buffer.append("interrupt request：").append(e.getMessage());
            }

            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;

                buffer.append(response.body().string());
                OkHttpUtils.getSemaphoreInstance().release();
                response.body().close();
            }
        });

        try {
            getSemaphoreInstance().acquire();
        } catch (InterruptedException e) {
            log.error("okHttpClient async", e);
        }

        return buffer.toString();
    }

    /**
     * 异步请示, 带有接口回调
     *
     * @param callBack
     */
    public void async(final ICallBack callBack) {
        this.setHeader(this.request);
        okHttpClient.newCall(this.request.build()).enqueue(new Callback() {
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                callBack.onSuccessful(call, IOUtils.toString(response.body().byteStream(), StandardCharsets.UTF_8));
            }

            public void onFailure(Call call, IOException e) {
                callBack.onFailure(call, e.getMessage());
            }
        });
    }
}
