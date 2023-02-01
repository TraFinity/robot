package xyz.turtlecase.robot.business.task.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.BaseMapper;

/**
 * task 执行表
 */
public interface TaskProcessMapper extends BaseMapper<TaskProcessPo> {
    /**
     * 查询任务表记录
     *
     * @param taskId
     * @return
     */
    @Select({"<script>",
            " SELECT s.task_id, s.id AS sub_task_id, t.type, t.contract, s.action, s.value, s.all_members, t.status " +
                    " FROM addons_task AS t " +
                    " INNER JOIN addons_task_condition AS s ON t.id = s.task_id " +
                    " WHERE t.id = #{taskId} AND (s.all_members is null or s.all_members = 0) ",
            "</script>"})
    List<TaskInfoPo> selectTaskInfo(@Param("taskId") Integer taskId);

    /**
     * 查询task进度表
     *
     * @param taskId
     * @return
     */
    @Select({" SELECT * FROM robot_task_process  WHERE task_id = #{taskId}"})
    List<TaskProcessPo> selectTaskProcess(@Param("taskId") Integer taskId);

    /**
     * 更新任务执行状态
     *
     * @param taskId
     * @param subTaskId
     * @param status
     * @return
     */
    @Update({"UPDATE robot_task_process set `status` = #{status} WHERE task_id =  #{taskId} AND sub_task_id =  #{subTaskId}"})
    int updateTaskProcessStatus(@Param("taskId") Integer taskId,
                                @Param("subTaskId") Integer subTaskId,
                                @Param("status") Integer status);

    /**
     * 更新原始任务状态
     *
     * @param taskId
     * @param status
     * @return
     */
    @Update({"UPDATE addons_task set `status` = #{status} WHERE id =  #{taskId} "})
    int updateTaskStatus(@Param("taskId") Integer taskId, @Param("status") Integer status);
}
