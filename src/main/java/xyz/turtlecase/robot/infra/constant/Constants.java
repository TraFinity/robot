package xyz.turtlecase.robot.infra.constant;

import xyz.turtlecase.robot.infra.utils.JsonUtils;
import xyz.turtlecase.robot.infra.web.RestResult;
import xyz.turtlecase.robot.infra.web.RestResultGenerator;

public final class Constants {
    /**
     * 是否生效的状态
     */
    public static final Integer STATUS_ENABLE = 1;
    /**
     * 无效状态
     */
    public static final Integer STATUS_DISABLED = 0;
    /**
     * 有效
     */
    public static final Integer STATUS_DEFAULT = 1;
    public static final RestResult<String> TOKEN_VERIFY_FAIL = RestResultGenerator.genResult(false, 401, "token verify fail", null);
    public static final String TOKEN_VERIFY_FAIL_JSON;
    public static final String TOKEN_VERIFY_NO_USER_JSON;
    public static final String TOKEN_VERIFY_EXPIRED_JSON;
    public static final String NO_ROLE_JSON;
    /**
     * 提示信息
     */
    public static final String MSG_NO_MEMBER_ID = "token invalid, can not get memberID";
    public static final String MSG_NO_WALLET = "token invalid, can not get wallet address";
    public static final String MSG_NO_TWITTER = "token invalid, can not get twitter";
    public static final String MSG_NO_ADDONS_USER_TYPE = "token invalid, can not get addonsUserType";
    public static final Integer APP_PUBLICLY_YES = 1;
    public static final Integer APP_PUBLICLY_NO = 0;
    public static final Integer GENDER_GIRL = 1;
    public static final Integer GENDER_MAN = 2;
    public static final Integer YES = 1;
    public static final Integer NO = 0;
    public static final Integer ETH_ADDRESS_LENGTH = 42;
    public static final String PRODUCT_CATEGORY_DEFAULT = "turtlecase";
    public static final String PRICE_CURRENCY_DEFAULT = "Ethereum";
    public static final String HTTP_CLIENT_HEADER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36";
    public static final String HTTP_CLIENT_HEADER_CONTENT_TYPE_JSON = "application/json; charset=utf-8";
    public static final String HTTP_NETWORK_ERROR_MESSAGE = "An exception occurred in the network request";
    public static final String VERIFIER = "oauth_verifier";
    public static final String HEADER = "Authorization";
    public static final String SCOPE = "scope";
    public static final String BASIC = "Basic";
    public static final String BEARER = "Bearer";
    /**
     * http header, 放置limit type, 主要用于twitter
     */
    public static final String HTTP_REQUEST_LIMIT_HEADER = "x-app-limit-key";
    public static final String DEFAULT_CHARSET = "UTF-8";
    public static RestResult<String> RESPONSE_SUCCESS = RestResultGenerator.genSuccessResult("done");

    static {
        TOKEN_VERIFY_FAIL_JSON = JsonUtils.objectToJson(TOKEN_VERIFY_FAIL);
        TOKEN_VERIFY_NO_USER_JSON = JsonUtils.objectToJson(RestResultGenerator.genResult(false, 401, "no user", (Object) null));
        TOKEN_VERIFY_EXPIRED_JSON = JsonUtils.objectToJson(RestResultGenerator.genResult(false, 401, "token expired", (Object) null));
        NO_ROLE_JSON = JsonUtils.objectToJson(RestResultGenerator.genResult(false, 401, "no privilege", (Object) null));
    }
}
