package xyz.turtlecase.robot.business.twitter.api.auth;

import lombok.Data;
import lombok.EqualsAndHashCode;
import xyz.turtlecase.robot.infra.utils.AssertUtil;

/**
 * 证书实体, 生成Http调用的证书串
 */
@Data
@EqualsAndHashCode
public class OAuth2AccessToken {
    private String accessToken;
    private String tokenType;
    private Integer expiresIn;
    private String refreshToken;
    private String scope;
    /**
     * 原始的response
     */
    private String rawResponse;

    public OAuth2AccessToken() {
    }

    public OAuth2AccessToken(String accessToken, String tokenType, Integer expiresIn, String refreshToken, String scope, String rawResponse) {
        AssertUtil.checkNotNull(accessToken, "access_token can't be null");
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
        this.scope = scope;
        this.rawResponse = rawResponse;
    }

    public void validate() {
        AssertUtil.checkNotNull(this.accessToken, " access_token can't be null");
        AssertUtil.checkNotNull(this.refreshToken, " refresh_token can't be null");
    }

    public String getAccessTokenBearer() {
        return "Bearer " + this.accessToken;
    }
}
