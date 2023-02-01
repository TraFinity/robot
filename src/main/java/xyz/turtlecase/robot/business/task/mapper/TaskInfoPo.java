package xyz.turtlecase.robot.business.task.mapper;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import xyz.turtlecase.robot.business.task.dto.TaskType;
import xyz.turtlecase.robot.business.task.model.TaskInfo;
import xyz.turtlecase.robot.business.task.model.TaskStatus;
import xyz.turtlecase.robot.business.twitter.biz.service.TwitterUtils;
import xyz.turtlecase.robot.infra.exception.BaseException;

@Data
@EqualsAndHashCode
public class TaskInfoPo extends TaskInfo {
    /**
     * 对象转换
     *
     * @return
     */
    public TaskProcessPo castTaskProcess() {
        TaskProcessPo taskProcessPo = new TaskProcessPo();
        taskProcessPo.setTaskId(this.getTaskId());
        taskProcessPo.setSubTaskId(this.getSubTaskId());
        taskProcessPo.setCreateTime(new Date());
        taskProcessPo.setUpdateTime(new Date());
        taskProcessPo.setContract(this.getContract());
        taskProcessPo.setType(this.getType());
        String action = null;
        String value = null;
        switch (this.getAction()) {
            case 1:
                if (this.getAllMembers() != null && this.getAllMembers().equals(1)) {
                    action = TaskType.TWITTER_FOLLOW_MEMBERS.name();
                    // todo 抽出所有要关注的twitter账号信息, 写到单独的表中
                } else {
                    action = TaskType.TWITTER_FOLLOW_MEMBER.name();
                    value = TwitterUtils.getUserName(this.getValue());
                }
                break;
            case 2:
                action = TaskType.TWITTER_REPLY_TWEET.name();
                value = TwitterUtils.getTweetId(this.getValue());
                break;
            case 3:
                action = TaskType.TWITTER_RE_TWEET.name();
                value = TwitterUtils.getTweetId(this.getValue());
                break;
            case 4:
                action = TaskType.TWITTER_LIKE_TWEET.name();
                value = TwitterUtils.getTweetId(this.getValue());
                break;
            default:
                throw new BaseException("Unsupported task action");
        }

        taskProcessPo.setAction(action);
        taskProcessPo.setValue(value);
        taskProcessPo.setAllMember(this.getAllMembers());
        taskProcessPo.setStatus(TaskStatus.PROCESSING.getStatus());
        return taskProcessPo;
    }
}
