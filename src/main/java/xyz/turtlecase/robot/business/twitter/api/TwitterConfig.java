package xyz.turtlecase.robot.business.twitter.api;

import com.github.scribejava.core.base64.Base64;

import java.nio.charset.Charset;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class TwitterConfig {
    @Value("#{systemProperties['twitter_api_key']}")
    private String apiKey;
    @Value("#{systemProperties['twitter_api_key_secret']}")
    private String apiKeySecret;
    @Value("#{systemProperties['twitter_bearer_token']}")
    private String bearerToken;
    @Value("#{systemProperties['twitter_access_token']}")
    private String accessToken;
    @Value("#{systemProperties['twitter_access_token_secret']}")
    private String accessTokenSecret;
    @Value("#{systemProperties['twitter_oAuth_client_id']}")
    private String oAuthClientID;
    @Value("#{systemProperties['twitter_oAuth_client_secret']}")
    private String oAuthClientSecret;
    @Value("#{systemProperties['twitter_oAuth_callback_url']}")
    private String oAuthCallBackUrl;
    @Value("#{systemProperties['twitter_user_id']}")
    private String userId;
    private String authenticationCode;
    private String bearAuthorizationToken;

    /**
     * 根据oAuth client ID和oAuth client secret生成base64串
     * @return
     */
    public String createAuthentication() {
        if (StringUtils.isBlank(this.authenticationCode)) {
            this.authenticationCode = "Basic "
                    + Base64.encode(String.format("%s:%s", this.oAuthClientID, this.oAuthClientSecret)
                    .getBytes(Charset.forName("UTF-8")));
        }

        return this.authenticationCode;
    }

    /**
     * 返回对应的APP bear token
     * @return
     */
    public String getBearAuthorizationToken() {
        if (StringUtils.isBlank(this.bearAuthorizationToken)) {
            this.bearAuthorizationToken = "Bearer " + this.bearerToken;
        }

        return this.bearAuthorizationToken;
    }

}
