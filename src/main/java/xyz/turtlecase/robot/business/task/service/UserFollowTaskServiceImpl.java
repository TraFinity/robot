package xyz.turtlecase.robot.business.task.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import xyz.turtlecase.robot.business.task.dto.TaskProcessDTO;
import xyz.turtlecase.robot.business.task.dto.TaskType;
import xyz.turtlecase.robot.business.twitter.biz.service.TwitterBizService;
import xyz.turtlecase.robot.infra.utils.CollectionUtils;

/**
 * follow指定账户
 */
@Validated
@Service
public class UserFollowTaskServiceImpl extends AbstractTaskService {
    @Autowired
    private TwitterBizService twitterBizService;

    public TaskType getTaskType() {
        return TaskType.TWITTER_FOLLOW_MEMBER;
    }

    public Set<String> twitterUserFilter(@NotNull TaskProcessDTO taskProcess) throws Exception {
        // follow无好的实现方法, 直接当做mock, 将db表中holder的twitter账号刷进去
        // todo 可以从源账户发起检查, 毕竟一个账号follow的数量不会太可怕
        List<String> twitters = twitterBizService.selectTwitterWithNFTContract(taskProcess.getContract());
        return CollectionUtils.isEmpty(twitters) ? new HashSet() : new HashSet(twitters);
    }
}
