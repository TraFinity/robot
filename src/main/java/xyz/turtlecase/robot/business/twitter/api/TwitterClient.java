package xyz.turtlecase.robot.business.twitter.api;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.TypeReference;

import java.io.IOException;
import java.util.List;
import javax.validation.constraints.NotBlank;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.turtlecase.robot.business.twitter.api.auth.TwitterCredentialsFactory;
import xyz.turtlecase.robot.business.twitter.api.dto.Tweet;
import xyz.turtlecase.robot.business.twitter.api.dto.TwitterBooleanData;
import xyz.turtlecase.robot.business.twitter.api.dto.TwitterList;
import xyz.turtlecase.robot.business.twitter.api.dto.TwitterResponse;
import xyz.turtlecase.robot.business.twitter.api.dto.TwitterUser;
import xyz.turtlecase.robot.infra.exception.RateLimitingException;
import xyz.turtlecase.robot.infra.service.RateLimitService;
import xyz.turtlecase.robot.infra.utils.http.okhttp.OkHttpResponse;
import xyz.turtlecase.robot.infra.utils.http.okhttp.OkHttpUtils;

import static xyz.turtlecase.robot.business.twitter.api.TwitterConstants.TWEET_FIELDS_PARAMETER_NAME;
import static xyz.turtlecase.robot.business.twitter.api.TwitterConstants.USER_FIELDS_SAMPLE;
import static xyz.turtlecase.robot.infra.constant.Constants.HTTP_REQUEST_LIMIT_HEADER;

/**
 * twitter接口封装
 * 具体限速更新, 查看:
 * <a href="https://developer.twitter.com/en/portal/products>portal/products</a>
 * <a href="https://developer.twitter.com/en/docs/twitter-api/rate-limits>twitter-api/rate-limits</a>
 *
 */
@Slf4j
public class TwitterClient {
    private final TwitterErrorHandler twitterErrorHandler = new TwitterErrorHandler();
    private final TwitterConfig twitterConfig;
    private final RateLimitService rateLimitService;

    public TwitterClient(TwitterConfig twitterConfig, RateLimitService rateLimitService) {
        assert twitterConfig != null;
        assert rateLimitService != null;
        this.twitterConfig = twitterConfig;
        this.rateLimitService = rateLimitService;
    }

    /**
     * 使用oAuth token操作
     * @param requestKey
     * @return
     */
    public OkHttpUtils getOkHttpUtilsForOAuthUser(String requestKey) {
        return OkHttpUtils.builder()
                .addHeader("Authorization", TwitterCredentialsFactory.getCredentials().getAccessTokenBearer())
                .addHeader(HTTP_REQUEST_LIMIT_HEADER, requestKey);
    }

    /**
     * 使用app token操作
     * @param requestKey
     * @return
     */
    public OkHttpUtils getOkHttpUtilsForApp(String requestKey) {
        // 检查是否被限流
        rateLimitService.checkRateLimit(requestKey);
        return OkHttpUtils.builder()
                .addHeader("Authorization", twitterConfig.getBearAuthorizationToken())
                .addHeader(HTTP_REQUEST_LIMIT_HEADER, requestKey);
    }

    /**
     * 根据用户名查询twitter账号信息
     * 900 requests/15 min PER USER
     * 300 requests/15 min PER APP
     * @param userName
     * @return
     * @throws IOException
     * @throws RateLimitingException
     */
    public TwitterUser findUserByUsername(String userName) throws IOException, RateLimitingException {
        String requestKey = "rate_limit_twitter_user_lookup";
        rateLimitService.checkRateLimit(requestKey);

        OkHttpResponse httpResponse = getOkHttpUtilsForApp(requestKey)
                .url("https://api.twitter.com/2/users/by/username/" + userName)
                .addParam(TwitterConstants.USER_FIELDS_PARAMETER_NAME, TwitterConstants.USER_FIELDS)
                .get()
                .sync(twitterErrorHandler);

        assert httpResponse != null;

        TwitterResponse<TwitterUser> response = JSON.parseObject(httpResponse.getBody(),
                new TypeReference<TwitterResponse<TwitterUser>>() {});
        if (response.getErrors() == null) {
            return response.getData();
        } else {
            log.warn("error findUserByUsername {}, response: {}", userName, httpResponse.getBody());
            return null;
        }
    }

    /**
     * 查询oAuth2.0 token的个人信息
     * 75 requests/15 mins PER USER
     * @return
     * @throws IOException
     * @throws RateLimitingException
     */
    public TwitterUser me() throws IOException, RateLimitingException {
        String requestKey = "rate_limit_twitter_user_me";
        OkHttpResponse httpResponse = getOkHttpUtilsForOAuthUser(requestKey)
                .url("https://api.twitter.com/2/users/me")
                .get()
                .sync(twitterErrorHandler);

        assert httpResponse != null;

        TwitterResponse<TwitterUser> response = JSON.parseObject(httpResponse.getBody(),
                new TypeReference<TwitterResponse<TwitterUser>>() {});
        if (response.getErrors() == null) {
            return response.getData();
        } else {
            log.warn("error me ,response: {}", httpResponse.getBody());
            return null;
        }
    }

    /**
     * 根据用户 id 查询twitter 账户信息
     * @param id
     * @return
     * @throws IOException
     * @throws RateLimitingException
     */
    public TwitterUser findUserById(String id) throws IOException, RateLimitingException {
        String requestKey = "rate_limit_twitter_user_lookup";
        OkHttpResponse httpResponse = getOkHttpUtilsForOAuthUser(requestKey)
                .url("https://api.twitter.com/2/users/" + id)
                .addParam(TwitterConstants.USER_FIELDS_PARAMETER_NAME, TwitterConstants.USER_FIELDS)
                .get()
                .sync(twitterErrorHandler);

        assert httpResponse != null;

        TwitterResponse<TwitterUser> response = JSON.parseObject(httpResponse.getBody(),
                new TypeReference<TwitterResponse<TwitterUser>>() {});

        if (response.getErrors() == null) {
            return response.getData();
        } else {
            log.warn("error findUserById {}, response: {}", id, httpResponse.getBody());
            return null;
        }
    }

    /**
     * Returns a lists of users who are followers of the specified user ID
     * 关注指定账户的账户列表, 有分页
     * 15 requests / 15 mins PER USER
     * 15 requests / 15 mins PER APP
     * @param userID twitterID
     * @param paginationToken 分页
     * @return
     * @throws IOException
     * @throws RateLimitingException
     */
    public TwitterResponse<List<TwitterUser>> usersIdFollowers(String userID, String paginationToken) throws IOException, RateLimitingException {
        String requestKey = "rate_limit_twitter_user_followers";
        OkHttpUtils okHttpUtils = getOkHttpUtilsForApp(requestKey)
                .addHeader(HTTP_REQUEST_LIMIT_HEADER, requestKey)
                .url("https://api.twitter.com/2/users/" + userID + "/followers")
                .addParam(TwitterConstants.USER_FIELDS_PARAMETER_NAME, TwitterConstants.USER_FIELDS)
                .addParam(TwitterConstants.MAX_RESULT_PARAMETER_NAME, TwitterConstants.MAX_RESULT);
        if (StringUtils.isNotBlank(paginationToken)) {
            okHttpUtils.addParam(TwitterConstants.PAGINATION_TOKEN_PARAMETER_NAME, paginationToken);
        }

        OkHttpResponse httpResponse = okHttpUtils.get().sync(twitterErrorHandler);

        assert httpResponse != null;

        TwitterResponse<List<TwitterUser>> response = JSON.parseObject(httpResponse.getBody(),
                new TypeReference<TwitterResponse<List<TwitterUser>>>() {});
        if (response.getErrors() == null) {
            return response;
        } else {
            log.warn("error findUserById {}, response: {}", userID, httpResponse.getBody());
            return null;
        }
    }

    /**
     * Returns a list of users the specified user ID is following
     * 查看账户关注的用户列表, 有分页
     * 15 requests / 15 mins PER USER
     * 15 requests / 15 mins PER APP
     * @param userID
     * @param paginationToken
     * @return
     * @throws IOException
     * @throws RateLimitingException
     */
    public TwitterResponse<List<TwitterUser>> usersIdFollowing(String userID, String paginationToken) throws IOException, RateLimitingException {
        String requestKey = "rate_limit_twitter_user_following";
        OkHttpUtils okHttpUtils = getOkHttpUtilsForApp(requestKey)
                .addHeader(HTTP_REQUEST_LIMIT_HEADER, requestKey)
                .url("https://api.twitter.com/2/users/" + userID + "/following")
                .addParam(TwitterConstants.USER_FIELDS_PARAMETER_NAME, TwitterConstants.USER_FIELDS)
                .addParam(TwitterConstants.MAX_RESULT_PARAMETER_NAME, "1000");

        if (StringUtils.isNotBlank(paginationToken)) {
            okHttpUtils.addParam(TwitterConstants.PAGINATION_TOKEN_PARAMETER_NAME, paginationToken);
        }

        OkHttpResponse httpResponse = okHttpUtils.get().sync(twitterErrorHandler);

        assert httpResponse != null;

        TwitterResponse<List<TwitterUser>> response = JSON.parseObject(httpResponse.getBody(),
                new TypeReference<TwitterResponse<List<TwitterUser>>>() {});

        if (response.getErrors() == null) {
            return response;
        } else {
            log.warn("error findUserById {}, response: {}", userID, httpResponse.getBody());
            return null;
        }
    }

    /**
     * 查看指定twitter账户的list信息(只能查询出public的记录), 只取第一页
     * 15 requests / 15 mins PER USER
     * 15 requests / 15 mins PER APP
     * @param userID
     * @param paginationToken
     * @return
     * @throws IOException
     * @throws RateLimitingException
     */
    public TwitterResponse<List<TwitterList>> listUserOwnedLists(String userID, String paginationToken) throws IOException, RateLimitingException {
        String requestKey = "rate_limit_twitter_user_list";
        OkHttpUtils okHttpUtils = getOkHttpUtilsForApp(requestKey)
                .url("https://api.twitter.com/2/users/" + userID + "/owned_lists")
                .addParam(TwitterConstants.MAX_RESULT_PARAMETER_NAME, "100")
                .addParam(TwitterConstants.LIST_FIELDS_PARAMETER_NAME, TwitterConstants.LIST_FIELDS);

        if (StringUtils.isNotBlank(paginationToken)) {
            okHttpUtils.addParam(TwitterConstants.PAGINATION_TOKEN_PARAMETER_NAME, paginationToken);
        }

        OkHttpResponse httpResponse = okHttpUtils.get().sync(twitterErrorHandler);

        assert httpResponse != null;

        TwitterResponse<List<TwitterList>> response = JSON.parseObject(httpResponse.getBody(),
                new TypeReference<TwitterResponse<List<TwitterList>>>() {});

        if (response.getErrors() == null) {
            return response;
        } else {
            log.warn("error listUserOwnedLists {}, paginationToken:{} response: {}", userID, paginationToken, httpResponse.getBody());
            return null;
        }
    }

    /**
     * 根据list ID获取list的信息
     * 75 requests / 15 mins PER USER
     * 75 requests / 15 mins PER APP
     * @param listID
     * @return
     * @throws IOException
     * @throws RateLimitingException
     */
    public TwitterList getListById(String listID) throws IOException, RateLimitingException {
        String requestKey = "rate_limit_twitter_list_get";
        OkHttpResponse httpResponse = getOkHttpUtilsForApp(requestKey)
                .url("https://api.twitter.com/2/lists/" + listID)
                .get()
                .sync(twitterErrorHandler);

        assert httpResponse != null;

        TwitterResponse<TwitterList> response = JSON.parseObject(httpResponse.getBody(),
                new TypeReference<TwitterResponse<TwitterList>>() {});

        if (response.getErrors() == null) {
            return response.getData();
        } else {
            log.warn("error getListById {}, response: {}", listID, httpResponse.getBody());
            return null;
        }
    }

    /**
     * 创建oAuth token对应账户下的list
     * 300 requests / 15 mins
     * @param listName list名
     * @param setPrivate 是否私有
     * @param description 描述
     * @return
     * @throws IOException
     * @throws RateLimitingException
     */
    public TwitterList createList(String listName, Boolean setPrivate, String description) throws IOException, RateLimitingException {
        if (setPrivate == null) {
            setPrivate = Boolean.FALSE;
        }

        String requestKey = "rate_limit_twitter_list_create";
        OkHttpResponse httpResponse = getOkHttpUtilsForOAuthUser(requestKey)
                .url("https://api.twitter.com/2/lists")
                .addHeader("content-type", "application/json")
                .addParam("name", listName)
                .addParam("description", description)
                .addParam("private", setPrivate)
                .post(true)
                .sync(twitterErrorHandler);

        assert httpResponse != null;

        TwitterResponse<TwitterList> response = JSON.parseObject(httpResponse.getBody(),
                new TypeReference<TwitterResponse<TwitterList>>() {});

        if (response.getErrors() == null) {
            return  response.getData();
        } else {
            log.warn("error createList, name: {}, response: {}", listName, httpResponse.getBody());
            return null;
        }
    }

    /**
     * 删除oAuth token对应的账户下的list
     * 300 requests / 15 mins PER USER
     * @param listID
     * @return
     * @throws IOException
     * @throws RateLimitingException
     */
    public Boolean listIdDelete(String listID) throws IOException, RateLimitingException {
        String requestKey = "rate_limit_twitter_list_delete";
        OkHttpResponse httpResponse = getOkHttpUtilsForOAuthUser(requestKey)
                .url("https://api.twitter.com/2/lists/" + listID)
                .delete()
                .sync(twitterErrorHandler);

        assert httpResponse != null;

        TwitterResponse<TwitterBooleanData> response = JSON.parseObject(httpResponse.getBody(), 
                new TypeReference<TwitterResponse<TwitterBooleanData>>() {
        });
        if (response.getErrors() == null) {
            return response.getData().getDeleted();
        } else {
            log.warn("error listIdDelete, listID: {}, response: {}", listID, httpResponse.getBody());
            return null;
        }
    }

    /**
     * 往oAuth token对应的账户下的list添加成员
     * 300 requests / 15 mins PER USER
     * @param listID
     * @param userID twitter USER ID
     * @return
     * @throws IOException
     * @throws RateLimitingException
     */
    public Boolean listAddMember(@NotBlank String listID, @NotBlank String userID) throws IOException, RateLimitingException {
        String requestKey = "rate_limit_twitter_list_member_add";
        OkHttpResponse httpResponse = getOkHttpUtilsForOAuthUser(requestKey)
                .url("https://api.twitter.com/2/lists/" + listID + "/members")
                .addParam("user_id", userID)
                .post(true)
                .sync(twitterErrorHandler);

        assert httpResponse != null;

        log.info(httpResponse.getBody());
        JSONObject jsonObject = JSON.parseObject(httpResponse.getBody());
        if(jsonObject.containsKey("data")){
            return jsonObject.getJSONObject("data").getBoolean("is_member");
        }

        return null;
    }

    /**
     * list删除成员(仅可操作oAuth token对应的账户下)
     * 300 requests / 15 mins PER USER
     * @param listID
     * @param userID twitter USER ID
     * @return
     * @throws IOException
     * @throws RateLimitingException
     */
    public Boolean listRemoveMember(@NotBlank String listID, @NotBlank String userID) throws IOException, RateLimitingException {
        String requestKey = "rate_limit_twitter_list_member_remove";
        OkHttpResponse httpResponse = getOkHttpUtilsForOAuthUser(requestKey)
                .url("https://api.twitter.com/2/lists/" + listID + "/members/" + userID)
                .delete()
                .sync(twitterErrorHandler);

        assert httpResponse != null;

        TwitterResponse<TwitterBooleanData> response =  JSON.parseObject(httpResponse.getBody(),
                new TypeReference<TwitterResponse<TwitterBooleanData>>() {
        });
        if (response.getErrors() == null) {
            return response.getData().getIs_member();
        } else {
            log.warn("error listRemoveMember, listID: {}, response: {}", listID, httpResponse.getBody());
            return null;
        }
    }

    /**
     * 查询tweet信息
     * @param tweetId
     * @return
     * @throws IOException
     * @throws RateLimitingException
     */
    public Tweet getTweet(@NotBlank String tweetId) throws IOException, RateLimitingException {
        String requestKey = "rate_limit_twitter_get_tweet_id";
        OkHttpResponse httpResponse = getOkHttpUtilsForApp(requestKey)
                .url("https://api.twitter.com/2/tweets/" + tweetId + "?tweet.fields=author_id,id,public_metrics")
                .delete()
                .sync(twitterErrorHandler);

        assert httpResponse != null;

        TwitterResponse<Tweet> response = JSON.parseObject(httpResponse.getBody(),
                new TypeReference<TwitterResponse<TwitterBooleanData>>() {
        });
        if (response.getErrors() == null) {
            return response.getData();
        } else {
            log.warn("error getTwitter, tweetId: {}, response: {}", tweetId, httpResponse.getBody());
            return null;
        }
    }

    /**
     * retweet的账户列表, 有分页
     * The maximum number of results to be returned per page. This can be a number between 1 and 100. By default, each page will return 100 results.
     * 75 requests / 15 mins PER USER
     * 75 requests / 15 mins PER APP
     * @param tweetId
     * @param paginationToken
     * @return
     * @throws IOException
     * @throws RateLimitingException
     */
    public TwitterResponse<List<TwitterUser>> retweetUsers(@NotBlank String tweetId, String paginationToken) throws IOException, RateLimitingException {
        String requestKey = "rate_limit_twitter_retweet_users";
        OkHttpUtils okHttpUtils = getOkHttpUtilsForApp(requestKey)
                .url("https://api.twitter.com/2/tweets/" + tweetId + "/retweeted_by")
                .addParam(TwitterConstants.USER_FIELDS_PARAMETER_NAME, USER_FIELDS_SAMPLE)
                .addParam(TwitterConstants.MAX_RESULT_PARAMETER_NAME, "100");

        if (StringUtils.isNotBlank(paginationToken)) {
            okHttpUtils.addParam(TwitterConstants.PAGINATION_TOKEN_PARAMETER_NAME, paginationToken);
        }

        OkHttpResponse httpResponse = okHttpUtils.get().sync(twitterErrorHandler);

        assert httpResponse != null;

        TwitterResponse<List<TwitterUser>> response = JSON.parseObject(httpResponse.getBody(),
                new TypeReference<TwitterResponse<List<TwitterUser>>>() {
        });
        if (response.getErrors() == null) {
            return response;
        } else {
            log.warn("error retweetedBy, http status {} tweetId: {}, response: {}", tweetId, httpResponse.getHttpStatus(), httpResponse.getBody());
            return null;
        }
    }

    /**
     * reply tweet的账户列表
     * The maximum number of results to be returned per page. This can be a number between 1 and 100. By default, each page will return 100 results.
     * 75 requests / 15 mins PER USER
     * 75 requests / 15 mins PER APP
     * @param tweetId
     * @param paginationToken
     * @return
     * @throws IOException
     * @throws RateLimitingException
     */
    public TwitterResponse<List<Tweet>> replyTweetUsers(@NotBlank String tweetId, String paginationToken) throws IOException, RateLimitingException {
        String requestKey = "rate_limit_twitter_tweet_search_recent";
        OkHttpUtils okHttpUtils = getOkHttpUtilsForApp(requestKey)
                .url("https://api.twitter.com/2/tweets/search/recent")
                .addParam(TWEET_FIELDS_PARAMETER_NAME, "author_id")
                .addParam("query", "conversation_id:" + tweetId)
                .addParam(TwitterConstants.MAX_RESULT_PARAMETER_NAME, "100");
        if (StringUtils.isNotBlank(paginationToken)) {
            okHttpUtils.addParam(TwitterConstants.PAGINATION_TOKEN_PARAMETER_NAME, paginationToken);
        }

        OkHttpResponse httpResponse = okHttpUtils.get().sync(twitterErrorHandler);

        assert httpResponse != null;

        TwitterResponse<List<Tweet>> response = JSON.parseObject(httpResponse.getBody(),
                new TypeReference<TwitterResponse<List<Tweet>>>() {
        });
        if (response.getErrors() == null) {
            return response;
        } else {
            log.warn("error retweetedBy, http status {} tweetId: {}, response: {}", tweetId, httpResponse.getHttpStatus(), httpResponse.getBody());
            return null;
        }
    }

    /**
     * like tweet的用户列表
     * @param tweetId
     * @param paginationToken
     * @return
     * @throws IOException
     * @throws RateLimitingException
     */
    public TwitterResponse<List<TwitterUser>> tweetLikingUsers(@NotBlank String tweetId, String paginationToken) throws IOException, RateLimitingException {
        String requestKey = "rate_limit_twitter_tweet_liking_users";
        OkHttpUtils okHttpUtils = getOkHttpUtilsForApp(requestKey)
                .url("https://api.twitter.com/2/tweets/" + tweetId + "/liking_users")
                .addParam(TwitterConstants.USER_FIELDS_PARAMETER_NAME, USER_FIELDS_SAMPLE)
                .addParam(TwitterConstants.MAX_RESULT_PARAMETER_NAME, "100");
        if (StringUtils.isNotBlank(paginationToken)) {
            okHttpUtils.addParam(TwitterConstants.PAGINATION_TOKEN_PARAMETER_NAME, paginationToken);
        }

        OkHttpResponse httpResponse = okHttpUtils.get().sync(twitterErrorHandler);

        assert httpResponse != null;

        TwitterResponse<List<TwitterUser>> response = JSON.parseObject(httpResponse.getBody(),
                new TypeReference<TwitterResponse<List<TwitterUser>>>() {
        });
        if (response.getErrors() == null) {
            return response;
        } else {
            log.warn("error tweetLikingUsers, http status {} tweetId: {}, response: {}", tweetId, httpResponse.getHttpStatus(), httpResponse.getBody());
            return null;
        }
    }
}
