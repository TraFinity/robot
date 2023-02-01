package xyz.turtlecase.robot.business.task.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import xyz.turtlecase.robot.business.task.dto.TaskProcessDTO;
import xyz.turtlecase.robot.business.task.dto.TaskType;
import xyz.turtlecase.robot.business.twitter.biz.mapper.TwitterUserPo;
import xyz.turtlecase.robot.business.twitter.biz.service.TwitterBizService;
import xyz.turtlecase.robot.infra.utils.CollectionUtils;

/**
 * tweet reply任务
 */
@Service
@Validated
public class TweetReplyTaskServiceImpl extends AbstractTaskService {
    @Autowired
    private TwitterBizService twitterBizService;

    @Override
    public TaskType getTaskType() {
        return TaskType.TWITTER_REPLY_TWEET;
    }

    @Override
    public Set<String> twitterUserFilter(@NotNull TaskProcessDTO taskProcess) throws Exception {
        Set<String> authorIds = twitterBizService.getReplyTweetUsers(taskProcess.getValue());
        if (CollectionUtils.isEmpty( authorIds)) {
            return new HashSet();
        }

        // 从db查询出holder的twitter信息
        List<TwitterUserPo> list = twitterBizService.selectTwitterWhoHoldNFT(taskProcess.getContract());

        // 没有holder记录, 直接返回
        if (CollectionUtils.isEmpty(list)) {
            return new HashSet();
        }

        int size = list.size();


        Map<String, String> map = new HashMap(size);

        for(TwitterUserPo twitterUser : list){
            if (!StringUtils.isBlank(twitterUser.getId())) {
                map.put(twitterUser.getId(), twitterUser.getUserName());
            }
        }

        Set<String> userNames = new HashSet(size);

        for(String s : authorIds){
            String u = map.get(s);
            if (StringUtils.isNotBlank(u)) {
                userNames.add(u);
            }
        }

        return userNames;
    }
}
