package xyz.turtlecase.robot.business.twitter.api.auth;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * twitter oAuth token
 */
@Data
@EqualsAndHashCode
public class AccessTokenVO {
    /**
     * token类型, bearer
     */
    private String token_type;

    /**
     * 过期时间, 单位秒
     */
    private Integer expires_in;

    /**
     * 访问token
     */
    private String access_token;

    /**
     * 刷新用的token
     */
    private String refresh_token;

    /**
     * 权限码
     */
    private String scope;

}
