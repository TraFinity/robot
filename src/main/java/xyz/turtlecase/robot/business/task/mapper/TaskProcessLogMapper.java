package xyz.turtlecase.robot.business.task.mapper;

import java.util.Date;
import java.util.Set;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.BaseMapper;

public interface TaskProcessLogMapper extends BaseMapper<TaskProcessLogPo> {
    /**
     * 新增子任务执行日志记录
     *
     * @param taskId
     * @param subTaskId
     * @param createTime
     * @param twitterUserNames
     * @return
     */
    @Insert({"<script>" +
            "insert into robot_task_process_log(task_id,sub_task_id,create_time,twitter_user_name) values " +
            " <foreach collection='twitterUserNames' item='t' separator=',' > " +
            "(#{taskId},#{subTaskId},#{createTime},#{t}) " +
            " </foreach>" +
            "</script>"})
    int batchInsert(@Param("taskId") Integer taskId, @Param("subTaskId") Integer subTaskId,
                    @Param("createTime") Date createTime, @Param("twitterUserNames") Set<String> twitterUserNames);

    /**
     * 汇总完成任务的账号
     *
     * @param taskId
     * @param createTime
     * @param subTaskCount
     * @return
     */
    @Insert({"<script>" +
            "INSERT INTO robot_task_result(task_id, create_time, twitter_user_name) " +
            "  SELECT #{taskId} AS task_id,  #{createTime} AS create_time,  twitter_user_name FROM " +
            "    (  SELECT twitter_user_name, count(twitter_user_name)  AS c " +
            "         FROM robot_task_process_log  WHERE task_id=#{taskId}  GROUP BY twitter_user_name) AS t " +
            "          WHERE t.c = #{subTaskCount} " +
            "</script>"})
    int completeTask(@Param("taskId") Integer taskId, @Param("createTime") Date createTime,
                     @Param("subTaskCount") Integer subTaskCount);

}
