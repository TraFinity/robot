package xyz.turtlecase.robot.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.turtlecase.robot.business.twitter.api.TwitterClient;
import xyz.turtlecase.robot.business.twitter.api.TwitterConfig;
import xyz.turtlecase.robot.business.twitter.api.auth.*;
import xyz.turtlecase.robot.business.twitter.biz.dto.TwitterListDTO;
import xyz.turtlecase.robot.business.twitter.biz.dto.TwitterUserDTO;
import xyz.turtlecase.robot.business.twitter.biz.service.TwitterBizService;
import xyz.turtlecase.robot.infra.constant.Constants;
import xyz.turtlecase.robot.infra.web.BaseController;
import xyz.turtlecase.robot.infra.web.RestResult;
import xyz.turtlecase.robot.infra.web.RestResultGenerator;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * twitter
 */
@Slf4j
@RestController
public class TwitterController extends BaseController {
    @Autowired
    private TwitterClient twitterClient;
    @Autowired
    private TwitterOAuth2Service twitterOAuth2Service;
    @Autowired
    private TwitterConfig twitterConfig;
    @Autowired
    private TwitterBizService twitterBizService;

    /**
     * twitter oAuth回调
     * @param state
     * @param code
     * @return
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping({"/twitter/oAuth/callback"})
    @ResponseBody
    public RestResult<String> callback(@RequestParam(name = "state") String state,
                                       @RequestParam(name = "code") String code) throws IOException, ExecutionException, InterruptedException {
        log.info("twitter oAuth2.0 callback, state:{} code:{}", state, code);
        TwitterAccessTokenRequestParam accessTokenRequestParam = TwitterAccessTokenRequestParam.builder()
                .code(code)
                .scope("offline.access tweet.read users.read follows.read list.read list.write")
                .codeVerifier("challenge")
                .grantType("authorization_code")
                .oAuthClientId(twitterConfig.getOAuthClientID())
                .oAuthClientSecret(twitterConfig.getOAuthClientSecret())
                .redirectUri(twitterConfig.getOAuthCallBackUrl())
                .build();
        OAuth2AccessToken oAuth2AccessToken = twitterOAuth2Service.getAccessToken(accessTokenRequestParam);
        TwitterCredentialsFactory.updateCredentials(oAuth2AccessToken);
        if(StringUtils.isAnyBlank(oAuth2AccessToken.getAccessToken(), oAuth2AccessToken.getRefreshToken())){
            log.error("twitter oAuth token error");
        }
        return Constants.RESPONSE_SUCCESS;
    }

    /**
     * twitter回调, 刷新token
     * @return
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping({"/twitter/oAuth/refresh"})
    @ResponseBody
    public RestResult<String> refresh() throws IOException, ExecutionException, InterruptedException {
        TwitterRefreshTokenRequestParam refreshTokenRequestParam = TwitterRefreshTokenRequestParam.builder()
                .oAuthClientId(twitterConfig.getOAuthClientID())
                .oAuthClientSecret(twitterConfig.getOAuthClientSecret())
                .build();
        OAuth2AccessToken oAuth2AccessToken = twitterOAuth2Service.refreshAccessToken(refreshTokenRequestParam, TwitterCredentialsFactory.getCredentials().getRefreshToken());
        TwitterCredentialsFactory.updateCredentials(oAuth2AccessToken);
        if(StringUtils.isAnyBlank(oAuth2AccessToken.getAccessToken(), oAuth2AccessToken.getRefreshToken())){
            log.error("twitter oAuth refresh token error");
        }
        return Constants.RESPONSE_SUCCESS;
    }

    /**
     * twitter user查询
     * @param userName
     * @return
     */
    @GetMapping({"/twitter/user/findUserByUsername"})
    @ResponseBody
    public RestResult<TwitterUserDTO> findUserByUsername(@RequestParam(name = "userName") String userName) {
        TwitterUserDTO twitterUserDTO = twitterBizService.getTwitterUser(userName);
        return RestResultGenerator.genSuccessResult(twitterUserDTO);
    }

    /**
     * twitter list添加用户
     * @param listName
     * @param userName
     * @return
     * @throws IOException
     */
    @PostMapping({"/twitter/list/{listName}/addMember/{userName}"})
    @ResponseBody
    public RestResult<TwitterListDTO> listAddMember(@PathVariable(name = "listName") String listName, @PathVariable(name = "userName") String userName) throws IOException {
        return RestResultGenerator.genSuccessResult(twitterBizService.addMemberToList(listName, userName));
    }

    /**
     * twitter查询list
     * @param listName
     * @return
     * @throws IOException
     */
    @GetMapping({"/twitter/list/{listName}"})
    @ResponseBody
    public RestResult<TwitterListDTO> getList(@PathVariable(name = "listName") String listName) throws IOException {
        return RestResultGenerator.genSuccessResult(twitterBizService.getOrCreateTwitterList(listName));
    }
}
