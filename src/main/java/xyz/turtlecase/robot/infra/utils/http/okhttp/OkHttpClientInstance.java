package xyz.turtlecase.robot.infra.utils.http.okhttp;

import java.util.concurrent.TimeUnit;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

/**
 * OKHttpClient单例
 */
public class OkHttpClientInstance {
    private OkHttpClient okHttpClient;

    private OkHttpClientInstance(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    public static OkHttpClient getOkHttpClient() {
        return OkHttpClientInstance.SingletonEnum.INSTANCE.getInstance().okHttpClient;
    }

    /**
     * 定义一个静态枚举类
     */
    enum SingletonEnum {
        INSTANCE;

        private OkHttpClientInstance instance;

        SingletonEnum() {
            TrustManager[] trustManagers = SimpleSSLSocketFactory.buildTrustManagers();

            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .addInterceptor(new LoggingInterceptor())
                    .addNetworkInterceptor(new RequestRateLimitInterceptor())
                    .connectTimeout(5L, TimeUnit.SECONDS)
                    .writeTimeout(10L, TimeUnit.SECONDS)
                    .readTimeout(10L, TimeUnit.SECONDS)
                    .callTimeout(10L, TimeUnit.SECONDS)
                    .sslSocketFactory(SimpleSSLSocketFactory.createSSLSocketFactory(trustManagers), (X509TrustManager) trustManagers[0])
                    .hostnameVerifier((hostName, session) -> true)
                    .retryOnConnectionFailure(true);
            OkHttpClient okHttpClient = builder.build();
            instance = new OkHttpClientInstance(okHttpClient);
        }

        public OkHttpClientInstance getInstance() {
            return INSTANCE.instance;
        }
    }
}
