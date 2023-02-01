package xyz.turtlecase.robot.business.task.service;

import java.util.Set;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import xyz.turtlecase.robot.business.task.dto.TaskProcessDTO;
import xyz.turtlecase.robot.business.task.dto.TaskType;
import xyz.turtlecase.robot.business.twitter.biz.service.TwitterBizService;

/**
 * tweet liking任务实现
 */
@Service
@Validated
public class TweetLikingTaskServiceImpl extends AbstractTaskService {
    @Autowired
    private TwitterBizService twitterBizService;

    public TaskType getTaskType() {
        return TaskType.TWITTER_LIKE_TWEET;
    }

    public Set<String> twitterUserFilter(@NotNull TaskProcessDTO taskProcess) throws Exception {
        return twitterBizService.getLikingTweetUsers(taskProcess.getValue());
    }
}
