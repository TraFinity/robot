package xyz.turtlecase.robot.business.twitter.biz.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import xyz.turtlecase.robot.business.twitter.api.TwitterClient;
import xyz.turtlecase.robot.business.twitter.api.TwitterConfig;
import xyz.turtlecase.robot.business.twitter.api.dto.Tweet;
import xyz.turtlecase.robot.business.twitter.api.dto.TwitterList;
import xyz.turtlecase.robot.business.twitter.api.dto.TwitterResponse;
import xyz.turtlecase.robot.business.twitter.api.dto.TwitterUser;
import xyz.turtlecase.robot.business.twitter.biz.dto.TwitterListDTO;
import xyz.turtlecase.robot.business.twitter.biz.dto.TwitterUserDTO;
import xyz.turtlecase.robot.business.twitter.biz.mapper.*;
import xyz.turtlecase.robot.infra.config.ConfigProperties;
import xyz.turtlecase.robot.infra.exception.BaseException;
import xyz.turtlecase.robot.infra.exception.RateLimitingException;
import xyz.turtlecase.robot.infra.service.RedisUtil;
import xyz.turtlecase.robot.infra.utils.AssertUtil;
import xyz.turtlecase.robot.infra.utils.BeanCopy;
import xyz.turtlecase.robot.infra.utils.CollectionUtils;

import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.*;

@Slf4j
@Validated
@Service
public class TwitterBizServiceImpl implements TwitterBizService {
    private final int TWITTER_LIST_NAME_LENGTH = 25;
    @Autowired
    private TwitterClient twitterClient;
    @Autowired
    private TwitterUserMapper twitterUserMapper;
    @Autowired
    private TwitterListMapper twitterListMapper;
    @Autowired
    private TwitterListUserMapper twitterListUserMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private TwitterConfig twitterConfig;
    @Autowired
    private ConfigProperties configProperties;

    /**
     * 获取list
     * todo 需要对slug合法性进行校验
     *
     * @param slug
     * @return
     */
    public TwitterListDTO getList(@NotBlank String slug) {
        String key = "twitter_list_slug:" + slug;
        TwitterListDTO twitterListDTO = redisUtil.getKey(key);

        if (twitterListDTO != null && StringUtils.isNotBlank(twitterListDTO.getId())) {
            return twitterListDTO;
        }

        // 缓存没有则从db查询
        TwitterListPo twitterListPo = new TwitterListPo();
        twitterListPo.setSlug(slug);
        twitterListPo = twitterListMapper.selectOne(twitterListPo);
        twitterListDTO = BeanCopy.copyBean(twitterListPo, TwitterListDTO.class);
        if (twitterListDTO != null && StringUtils.isNotBlank(twitterListDTO.getId())) {
            redisUtil.setKey(key, twitterListDTO);
        }

        return twitterListDTO;
    }

    /**
     * 生成唯一的list name, 确保长度在[1,25]
     *
     * @param slug
     * @param subLength
     * @return
     */
    private String subSlugToListName(String slug, int subLength) {
        if (subLength > TWITTER_LIST_NAME_LENGTH) {
            throw new IllegalArgumentException("sub length must < 25");
        } else if (StringUtils.length(slug) <= TWITTER_LIST_NAME_LENGTH) {
            return slug;
        } else {
            int subLengthCopy = subLength;
            if (subLength == 0) {
                subLengthCopy = TWITTER_LIST_NAME_LENGTH;
            }

            // twitter list name 长度约束在[1,25], 按前面15个字符, 后面10个字符拆, 为啥要这么做? 避免重复, 且能望文生意
            return StringUtils.substring(slug, 0, subLengthCopy);
        }
    }

    /**
     * 查询账户信息, 主要是拿到twitter user ID, 如果dh没有则从twitter api查
     *
     * @param userName
     * @return
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public TwitterUserDTO getTwitterUser(@NotBlank String userName) {
        String key = "twitter_user_name:" + userName;
        TwitterUserDTO twitterUserDTO = redisUtil.getKey(key);

        if (twitterUserDTO != null && StringUtils.isNotBlank(twitterUserDTO.getId())) {
            return twitterUserDTO;
        }

        // 缓存没有则从db查询
        TwitterUserPo twitterUserPo = new TwitterUserPo();
        twitterUserPo.setUserName(userName);
        twitterUserPo = twitterUserMapper.selectOne(twitterUserPo);

        // db没有则从twitter api查询
        if (twitterUserPo == null) {
            try {
                TwitterUser user = twitterClient.findUserByUsername(userName);
                if (user != null) {
                    twitterUserPo = new TwitterUserPo();
                    twitterUserPo.setId(user.getId());
                    twitterUserPo.setUserName(user.getUsername());
                    twitterUserMapper.insertOnDuplicateUpdate(twitterUserPo);
                }
            } catch (Exception var6) {
                log.error("Error run getTwitterUser {}", userName, var6);
                throw new BaseException("Network error get twitter user info");
            }
        }

        if (twitterUserPo == null) {
            return null;
        } else {
            twitterUserDTO = new TwitterUserDTO();
            twitterUserDTO.setId(twitterUserPo.getId());
            twitterUserDTO.setUserName(twitterUserPo.getUserName());
            redisUtil.setKey(key, twitterUserDTO);
            return twitterUserDTO;
        }
    }

    /**
     * 获取list信息, 如果list不存在则新增
     *
     * @param slug
     * @return
     * @throws IOException
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public TwitterListDTO getOrCreateTwitterList(@NotBlank String slug) throws IOException {

        // 先查询, 若有db记录则返回
        TwitterListDTO twitterListDTO = getList(slug);
        if (twitterListDTO != null) {
            return twitterListDTO;
        }

        /*
        若无记录, 则往twitter api调list 添加, twitter list允许name重名, 所以也无所谓重复了
         */
        String listName = null;
        // 区分线上与开发使用
        if (!configProperties.isPrdEnv()) {
            listName = subSlugToListName(slug, 23) + "_t";
        } else {
            listName = subSlugToListName(slug, 25);
        }

        String key = "twitter_list_slug:" + slug;
        log.info("call twitter api to create list: {}", listName);


        TwitterList twitterList = twitterClient.createList(listName, Boolean.FALSE, listName);
        if (twitterList != null && StringUtils.isNotBlank(twitterList.getId())) {
            TwitterListPo twitterListPo = new TwitterListPo();
            twitterListPo.setId(twitterList.getId());
            twitterListPo.setName(listName);
            twitterListPo.setSlug(slug);
            twitterListPo.setUserId(twitterConfig.getUserId());
            twitterListMapper.insert(twitterListPo);
            twitterListDTO = new TwitterListDTO();
            twitterListDTO.setId(twitterListPo.getId());
            twitterListDTO.setName(twitterListPo.getName());
            twitterListDTO.setSlug(slug);
            redisUtil.setKey(key, twitterListDTO);
        }

        return twitterListDTO;
    }

    /**
     * 获取指定账号的followers
     *
     * @param userName
     * @return
     * @throws IOException
     * @throws RateLimitingException
     */
    @Override
    public Set<String> getUsersFollowers(String userName) throws IOException, RateLimitingException {
        Set<String> twitterUserSet = new HashSet(100);
        TwitterUserDTO twitterUserDTO = getTwitterUser(userName);
        if (twitterUserDTO == null) {
            return new HashSet();
        }

        TwitterResponse<List<TwitterUser>> response = twitterClient.usersIdFollowers(twitterUserDTO.getId(), (String) null);

        // 有数据且不为空
        if (response != null && !CollectionUtils.isEmpty(response.getData())) {

            for (TwitterUser user : response.getData()) {
                twitterUserSet.add(user.getUsername());
            }

            Boolean hasData = Boolean.TRUE;

            while (hasData && (!TwitterUtils.isLastPagination(response))) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException var8) {
                    log.warn("thread sleep error", var8);
                }

                response = twitterClient.usersIdFollowers(twitterUserDTO.getId(), response.getMeta().getNext_token());
                if (response != null && !CollectionUtils.isEmpty(response.getData())) {
                    for (TwitterUser user : response.getData()) {
                        twitterUserSet.add(user.getUsername());
                    }
                } else {
                    hasData = Boolean.FALSE;
                }
            }
        }
        return twitterUserSet;
    }


    /**
     * 获取liking-tweet的用户列表
     *
     * @param tweetId
     * @return
     * @throws IOException
     * @throws RateLimitingException
     */
    @Override
    public Set<String> getLikingTweetUsers(String tweetId) throws IOException, RateLimitingException {
        Set<String> twitterUserSet = new HashSet(100);
        TwitterResponse<List<TwitterUser>> response = twitterClient.tweetLikingUsers(tweetId, null);

        // 有数据且不空
        if (response != null && !CollectionUtils.isEmpty(response.getData())) {
            for (TwitterUser user : response.getData()) {
                twitterUserSet.add(user.getUsername());
            }

            Boolean hasData = Boolean.TRUE;

            while (hasData && !TwitterUtils.isLastPagination(response)) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException var7) {
                    log.warn("thread sleep error", var7);
                }

                response = twitterClient.tweetLikingUsers(tweetId, response.getMeta().getNext_token());
                if (response != null && !CollectionUtils.isEmpty(response.getData())) {
                    for (TwitterUser user : response.getData()) {
                        twitterUserSet.add(user.getUsername());
                    }
                } else {
                    hasData = Boolean.FALSE;
                }
            }
        }
        return twitterUserSet;

    }

    /**
     * 获取reply-tweet的用户列表
     *
     * @param tweetId
     * @return
     * @throws IOException
     * @throws RateLimitingException
     */
    @Override
    public Set<String> getReplyTweetUsers(String tweetId) throws IOException, RateLimitingException {
        Set<String> twitterUserIdSet = new HashSet(100);
        TwitterResponse<List<Tweet>> response = twitterClient.replyTweetUsers(tweetId, (String) null);
        if (response != null && !CollectionUtils.isEmpty( response.getData())) {
            for (Tweet tweet : response.getData()) {
                twitterUserIdSet.add(tweet.getAuthor_id());
            }

            Boolean hasData = Boolean.TRUE;

            // 翻页循环, 如果不是最后一页, 继续查询. 但要防止限流
            while (hasData && !TwitterUtils.isLastPagination(response)) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException var7) {
                    log.warn("thread sleep error", var7);
                }

                response = twitterClient.replyTweetUsers(tweetId, response.getMeta().getNext_token());
                if (response != null && !CollectionUtils.isEmpty(response.getData())) {
                    for (Tweet tweet : response.getData()) {
                        twitterUserIdSet.add(tweet.getAuthor_id());
                    }
                } else {
                    hasData = Boolean.FALSE;
                }
            }
        }
        return twitterUserIdSet;
    }


    /**
     * 获取reply-tweet的用户列表
     * @param tweetId
     * @return
     * @throws IOException
     * @throws RateLimitingException
     */
    @Override
    public Set<String> getReTweetUsers(@NotBlank String tweetId) throws IOException, RateLimitingException {
        Set<String> twitterUserSet = new HashSet(100);
        TwitterResponse<List<TwitterUser>> response = twitterClient.retweetUsers(tweetId, null);
        if (response != null && !CollectionUtils.isEmpty(response.getData())) {
            for (TwitterUser user : response.getData()) {
                twitterUserSet.add(user.getUsername());
            }

            Boolean hasData = Boolean.TRUE;

            while (hasData && !TwitterUtils.isLastPagination(response)) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException var7) {
                    log.warn("thread sleep error", var7);
                }

                response = twitterClient.retweetUsers(tweetId, response.getMeta().getNext_token());
                if (response != null && !CollectionUtils.isEmpty( response.getData())) {
                    for (TwitterUser user : response.getData()) {
                        twitterUserSet.add(user.getUsername());
                    }
                } else {
                    hasData = Boolean.FALSE;
                }
            }
        }

        return twitterUserSet;
    }


    /**
     * 查询出用户明细, 包含twitter user name及id(id可能为空)
     * @param contract 合约地址
     * @return
     */
    @Override
    public List<TwitterUserPo> selectTwitterWhoHoldNFT(@NotBlank String contract) {
        List<TwitterUserPo> list = twitterUserMapper.selectUserHoldNFTContract(contract);
        if (CollectionUtils.isEmpty( list)) {
            return list;
        }

        // 将id为空的记录填充进去
        for(TwitterUserPo twitterUserPo : list){
            if (StringUtils.isBlank(twitterUserPo.getId())) {
                TwitterUserDTO twitterUserDTO = getTwitterUser(twitterUserPo.getUserName());
                if (twitterUserDTO != null) {
                    twitterUserPo.setId(twitterUserDTO.getId());
                }
            }
        }

        return list;
    }

    /**
     * 查询出合约holder的twitter账号列表
     * @param contract
     * @return
     */
    @Override
    public List<String> selectTwitterWithNFTContract(@NotBlank String contract) {
        return twitterUserMapper.selectTwitterWithNFTContract(contract);
    }


    /**
     * 添加twitter成员到list
     * @param slug
     * @param twitterUserName
     * @return
     * @throws IOException
     * @throws RateLimitingException
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public TwitterListDTO addMemberToList(@NotBlank String slug, @NotBlank String twitterUserName) throws IOException, RateLimitingException {
        String key = "twitter_list_user::" + slug + "_" + twitterUserName;
        TwitterListDTO twitterListDTO = redisUtil.getKey(key);
        if (twitterListDTO != null) {
            return twitterListDTO;
        }

        twitterListDTO = getOrCreateTwitterList(slug);
        AssertUtil.checkNotNull(twitterListDTO, "can not find twitter list " + slug);


        TwitterUserDTO twitterUserDTO = getTwitterUser(twitterUserName);
        if (twitterUserDTO == null) {
            return null;
        }

        TwitterListUserPo query = new TwitterListUserPo();
        query.setListId(twitterListDTO.getId());
        query.setUserId(twitterUserDTO.getId());
        query = twitterListUserMapper.selectOne(query);
        if (query != null) {
            redisUtil.setKey(key, twitterListDTO);
            return twitterListDTO;
        }

        // 添加成员
        twitterClient.listAddMember(twitterListDTO.getId(), twitterUserDTO.getId());

        // 插值入表
        TwitterListUserPo record = new TwitterListUserPo();
        record.setListId(twitterListDTO.getId());
        record.setUserId(twitterUserDTO.getId());
        record.setUserName(twitterUserName);
        twitterListUserMapper.insert(record);
        redisUtil.setKey(key, twitterListDTO);
        return twitterListDTO;

    }
}
