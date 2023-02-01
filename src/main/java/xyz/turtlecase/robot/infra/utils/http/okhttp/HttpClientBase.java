package xyz.turtlecase.robot.infra.utils.http.okhttp;

import okhttp3.OkHttpClient;

public class HttpClientBase {
    protected OkHttpClient getOkHttpClient() {
        return OkHttpClientInstance.getOkHttpClient();
    }
}
