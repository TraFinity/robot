package xyz.turtlecase.robot.business.twitter.api.auth;

import com.github.scribejava.core.base64.Base64;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.nio.charset.Charset;

@Data
@EqualsAndHashCode
@Validated
@Builder
public class TwitterRefreshTokenRequestParam {

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
    private String oAuthClientSecret;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private String authenticationCode;

    /**
     * 生成http header使用的token
     * @return
     */
    public String createAuthentication() {
        if (StringUtils.isBlank(authenticationCode)) {
            authenticationCode = "Basic "
                    + Base64.encode(String.format("%s:%s", getOAuthClientId(), getOAuthClientSecret())
                    .getBytes(Charset.forName("UTF-8")));
        }

        return authenticationCode;
    }

}
