package xyz.turtlecase.robot.business.twitter.api.auth;

import com.alibaba.fastjson2.JSON;
import com.github.scribejava.core.utils.Preconditions;

import java.io.IOException;
import java.net.URLEncoder;
import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import xyz.turtlecase.robot.infra.exception.BaseException;
import xyz.turtlecase.robot.infra.utils.http.okhttp.OkHttpClientInstance;

import static xyz.turtlecase.robot.infra.constant.Constants.DEFAULT_CHARSET;
import static xyz.turtlecase.robot.infra.constant.Constants.HTTP_CLIENT_HEADER_AGENT;

/**
 * This is an example of getting an OAuth2 access token and using it to call an API.
 * It's expected to set TWITTER_OAUTH2_CLIENT_ID & TWITTER_OAUTH2_CLIENT_SECRET in TwitterCredentialsOAuth2
 * <p>
 * Example steps:
 * 1. Getting the App Authorization URL.
 * 2. User should click the URL and authorize it.
 * 3. After receiving the access token, setting the values into TwitterCredentialsOAuth2.
 * 4. Call the API.
 */
@Slf4j
@Service
@Validated
public class TwitterOAuth2Service {
    private static final String OAUTH_TOKEN_URL = "https://api.twitter.com/2/oauth2/token";

    /**
     * 获取访问权限的oAuth token
     * @param accessTokenRequestParam
     * @return
     * @throws IOException
     */
    public OAuth2AccessToken getAccessToken(@Valid TwitterAccessTokenRequestParam accessTokenRequestParam) throws IOException {
        StringBuilder url = new StringBuilder(OAUTH_TOKEN_URL);
        url.append("?code=").append(URLEncoder.encode(accessTokenRequestParam.getCode(), DEFAULT_CHARSET))
                .append("&redirect_uri=").append(URLEncoder.encode(accessTokenRequestParam.getRedirectUri(), DEFAULT_CHARSET))
                .append("&scope=").append(URLEncoder.encode(accessTokenRequestParam.getScope(), DEFAULT_CHARSET))
                .append("&grant_type=").append(URLEncoder.encode(accessTokenRequestParam.getGrantType(), DEFAULT_CHARSET))
                .append("&code_verifier=").append(URLEncoder.encode(accessTokenRequestParam.getCodeVerifier(), DEFAULT_CHARSET));

        RequestBody requestBody = (new FormBody.Builder()).build();
        Request request = new Request.Builder()
                .url(url.toString())
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("User-Agent", HTTP_CLIENT_HEADER_AGENT)
                .addHeader("Authorization", accessTokenRequestParam.createAuthentication())
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = OkHttpClientInstance.getOkHttpClient();
        Response response = okHttpClient.newCall(request).execute();

        assert response.body() != null;

        // 不要用body().toString()方法
        String body = response.body().string();
        response.close();

        // 对返回结果进行处理
        Preconditions.checkEmptyString(body, "Response body is incorrect. Can't extract a token from an empty string");
        if (!response.isSuccessful()) {
            log.error("fail to get twitter access token, http status {} body {}", response.code(), body);
            throw new BaseException("fail to get twitter access token");
        } else {

            AccessTokenVO accessToken = JSON.parseObject(body, AccessTokenVO.class);
            assert accessToken != null;
            return new OAuth2AccessToken(accessToken.getAccess_token(), accessToken.getToken_type(),
                    accessToken.getExpires_in(), accessToken.getRefresh_token(), accessToken.getScope(), body);
        }
    }

    /**
     * 刷新oAuth2.0 token
     * @param refreshTokenRequestParam
     * @param refreshToken
     * @return
     * @throws IOException
     */
    public OAuth2AccessToken refreshAccessToken(@Valid TwitterRefreshTokenRequestParam refreshTokenRequestParam, String refreshToken) throws IOException {

        StringBuilder url = new StringBuilder(OAUTH_TOKEN_URL);
        url.append("?refresh_token=").append(URLEncoder.encode(refreshToken, DEFAULT_CHARSET))
                .append("&grant_type=").append(URLEncoder.encode("refresh_token", DEFAULT_CHARSET))
                .append("&client_id=").append(URLEncoder.encode(refreshTokenRequestParam.getOAuthClientId(), DEFAULT_CHARSET));

        RequestBody requestBody = new FormBody.Builder().build();
        Request request = new Request.Builder()
                .url(url.toString())
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("User-Agent", HTTP_CLIENT_HEADER_AGENT)
                .addHeader("Authorization", refreshTokenRequestParam.createAuthentication())
                .post(requestBody)
                .build();

        OkHttpClient okHttpClient = OkHttpClientInstance.getOkHttpClient();
        Response response = okHttpClient.newCall(request).execute();

        assert response.body() != null;

        // 不要用body().toString()方法
        String body = response.body().string();
        response.close();
        Preconditions.checkEmptyString(body, "Response body is incorrect. Can't extract a token from an empty string");
        AccessTokenVO accessToken = JSON.parseObject(body, AccessTokenVO.class);

        assert accessToken != null;

        return new OAuth2AccessToken(accessToken.getAccess_token(), accessToken.getToken_type(),
                accessToken.getExpires_in(), accessToken.getRefresh_token(), accessToken.getScope(), body);
    }
}
