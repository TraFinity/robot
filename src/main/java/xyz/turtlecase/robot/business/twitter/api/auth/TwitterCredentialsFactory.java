package xyz.turtlecase.robot.business.twitter.api.auth;

import com.alibaba.fastjson2.JSON;
import org.apache.commons.lang3.StringUtils;
import xyz.turtlecase.robot.infra.service.RedisUtil;
import xyz.turtlecase.robot.infra.utils.AESUtil;
import xyz.turtlecase.robot.infra.utils.AssertUtil;
import xyz.turtlecase.robot.infra.utils.CommonUtil;
import xyz.turtlecase.robot.infra.utils.SpringBeanUtils;

/**
 * twitter凭证工厂
 */
public class TwitterCredentialsFactory {
    private OAuth2AccessToken accessToken;

    public static OAuth2AccessToken getCredentials() {
        return TwitterCredentialsFactory.SingletonEnum.INSTANCE.factory.accessToken;
    }

    /**
     * 更新证书
     * @param accessToken
     */
    public static void updateCredentials(OAuth2AccessToken accessToken) {
        TwitterCredentialsFactory.SingletonEnum.INSTANCE.updateToken(accessToken);
    }

    enum SingletonEnum {
        INSTANCE;

        private TwitterCredentialsFactory factory = new TwitterCredentialsFactory();

        SingletonEnum() {
            RedisUtil redisUtil = SpringBeanUtils.getBean(RedisUtil.class);
            String tokenString = redisUtil.getKey("system_twitter_credentials");
            if (StringUtils.isNotBlank(tokenString)) {
                String decode = AESUtil.decode(CommonUtil.getEnv("AES_Key"), tokenString);
                OAuth2AccessToken accessToken = JSON.parseObject(decode, OAuth2AccessToken.class);
                factory.accessToken = accessToken;
            }

        }

        public TwitterCredentialsFactory getInstance() {
            return factory;
        }

        /**
         * 更新token
         * @param accessToken
         */
        public void updateToken(OAuth2AccessToken accessToken) {
            AssertUtil.checkNotNull(accessToken, "access token is required");
            accessToken.validate();
            String tokenJson = JSON.toJSONString(accessToken);
            String encode = AESUtil.encode(CommonUtil.getEnv("AES_Key"), tokenJson);
            RedisUtil redisUtil = SpringBeanUtils.getBean(RedisUtil.class);
            redisUtil.setKey("system_twitter_credentials", encode);
            factory.accessToken = accessToken;
        }
    }
}
