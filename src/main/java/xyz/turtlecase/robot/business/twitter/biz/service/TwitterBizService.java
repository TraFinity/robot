package xyz.turtlecase.robot.business.twitter.biz.service;

import org.springframework.validation.annotation.Validated;
import xyz.turtlecase.robot.business.twitter.biz.dto.TwitterListDTO;
import xyz.turtlecase.robot.business.twitter.biz.dto.TwitterUserDTO;
import xyz.turtlecase.robot.business.twitter.biz.mapper.TwitterUserPo;
import xyz.turtlecase.robot.infra.exception.RateLimitingException;

import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * twitter 业务接口
 */
@Validated
public interface TwitterBizService {
    /**
     * 查询账户信息, 主要是拿到twitter user ID, 如果dh没有则从twitter api查
     * @param userName
     * @return
     */
    TwitterUserDTO getTwitterUser(@NotBlank String userName);

    /**
     * 校验list name是否在db存在
     * @param slug
     * @return
     */
    TwitterListDTO getList(@NotBlank String slug);

    /**
     * 获取list信息, 如果list不存在则新增
     * @param slug
     * @return
     * @throws IOException
     */
    TwitterListDTO getOrCreateTwitterList(@NotBlank String slug) throws IOException;

    /**
     * 添加twitter成员到list
     * @param slug
     * @param twitterUserName
     * @return
     * @throws IOException
     * @throws RateLimitingException
     */
    TwitterListDTO addMemberToList(@NotBlank String slug, @NotBlank String twitterUserName) throws IOException, RateLimitingException;

    /**
     * 查询出合约holder的twitter账号列表
     * @param contract
     * @return
     */
    List<String> selectTwitterWithNFTContract(@NotBlank String contract);

    /**
     * 查询出用户明细, 包含twitter user name及id(id可能为空)
     * @param contract 合约地址
     * @return
     */
    List<TwitterUserPo> selectTwitterWhoHoldNFT(@NotBlank String contract);

    /**
     * 获取reply-tweet的用户列表
     * @param tweetId
     * @return
     * @throws IOException
     * @throws RateLimitingException
     */
    Set<String> getReTweetUsers(@NotBlank String tweetId) throws IOException, RateLimitingException;

    /**
     * 获取reply-tweet的用户列表
     * @param tweetId
     * @return
     * @throws IOException
     * @throws RateLimitingException
     */
    Set<String> getReplyTweetUsers(@NotBlank String tweetId) throws IOException, RateLimitingException;

    /**
     * 获取liking-tweet的用户列表
     * @param tweetId
     * @return
     * @throws IOException
     * @throws RateLimitingException
     */
    Set<String> getLikingTweetUsers(@NotBlank String tweetId) throws IOException, RateLimitingException;

    /**
     * 获取指定账号的followers
     * @param userName
     * @return
     * @throws IOException
     * @throws RateLimitingException
     */
    Set<String> getUsersFollowers(@NotBlank String userName) throws IOException, RateLimitingException;
}
