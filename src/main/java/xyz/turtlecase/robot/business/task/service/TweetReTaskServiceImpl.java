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
 * re-tweet 任务
 */
@Service
@Validated
public class TweetReTaskServiceImpl extends AbstractTaskService {
    @Autowired
    private TwitterBizService twitterBizService;

    @Override
    public TaskType getTaskType() {
        return TaskType.TWITTER_RE_TWEET;
    }

    @Override
    public Set<String> twitterUserFilter(@NotNull TaskProcessDTO taskProcess) throws Exception {
        return twitterBizService.getReTweetUsers(taskProcess.getValue());
    }
}
