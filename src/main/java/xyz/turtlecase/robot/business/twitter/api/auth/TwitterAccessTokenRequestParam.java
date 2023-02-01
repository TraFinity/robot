package xyz.turtlecase.robot.business.twitter.api.auth;

import com.github.scribejava.core.base64.Base64;

import java.nio.charset.Charset;
import javax.validation.constraints.NotBlank;

import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;

@Data
@EqualsAndHashCode
@Builder
@Validated
public class TwitterAccessTokenRequestParam {
    /**
     * oAuth2.0 Client ID, 从twitter developer portal中获取, 需要设置User authentication settings
     * @see <a href="https://developer.twitter.com/en/portal/projects/1564918386223550464/apps/25484267/settings">developer portal</a>
     */
    @NotBlank
    private String oAuthClientId;

    /**
     * oAuth2.0 Client Secret
     */
    @NotBlank
    private  String oAuthClientSecret;

    /**
     *请示跳转到twitter后回调传递的code
     */
    @NotBlank
    private  String code;

    /**
     * 回调本地的url
     */
    @NotBlank
    private String redirectUri;

    /**
     * 授权范围, 可以查看对应的接口及权限
     * @see <a href="https://developer.twitter.com/en/docs/authentication/guides/v2-authentication-mapping">v2-authentication-mapping</a>
     */
    @NotBlank
    private  String scope;

    /**
     * 授权类型
     */
    @NotBlank
    private String grantType;

    /**
     * 校验码, 与生成twitter授权url时填写的一致
     */
    @NotBlank
    private  String codeVerifier;


    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private String authenticationCode;


    public String createAuthentication() {
        if (StringUtils.isBlank(this.authenticationCode)) {
            this.authenticationCode = "Basic "
                    + Base64.encode(String.format("%s:%s", this.getOAuthClientId(), this.getOAuthClientSecret())
                    .getBytes(Charset.forName("UTF-8")));
        }

        return this.authenticationCode;
    }
}
