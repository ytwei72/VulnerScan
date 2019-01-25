package com.dky.vulnerscan.dao;

import java.util.List;

import com.dky.vulnerscan.entity.TaskResult;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Repository;
//import com.dky.vulnerscan.entity.TaskResult;

@Repository
@Mapper
public interface TaskResultDao {
    //删除指定项目对应的所有任务结果
    @Delete("delete from task_result where project_id=#{projectID}")
    void deleteProjectTaskResult(@Param("projectID") int projectID);

    // 保存任务的结果
    @Insert("replace into task_result values(#{taskID},#{projectID},#{ip},#{ipInfo})")
    void saveTaskResult(@Param("taskID") int taskID, @Param("projectID") int projectID, @Param("ip") String ip, @Param("ipInfo") String ipInfo);

    //获取每个任务对应的资产数
    @Select("select count(*) from task_result where task_id=#{taskID} and project_id=#{projectID}")
    int getTaskPropertyNum(@Param("taskID") int taskID, @Param("projectID") int projectID);

    //获取指定任务对应的结果
    @Results(value = {
            @Result(id = true, property = "taskID", column = "task_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(id = true, property = "projectID", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(id = true, property = "ipAddr", column = "ip_addr", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "ipInfo", column = "ip_info", javaType = String.class, jdbcType = JdbcType.VARCHAR)})
    @Select("select * from task_result where task_id=#{taskID} and project_id=#{projectID}")
    List<TaskResult> getTaskResult(@Param("taskID") int taskID, @Param("projectID") int projectID);

    //删除指定任务对应的结果
    @Delete("delete from task_result where task_id=#{taskID}")
    void deleteTaskResult(@Param("taskID") int taskID);

    //获取ipInfo
    @Select("select ip_info from task_result where task_id=#{taskID} and project_id=#{projectID} and ip_addr=#{ip}")
    String getIpInfo(@Param("projectID") int projectID, @Param("taskID") int taskID, @Param("ip") String ip);

    //保存深度扫描的结果
    @Insert("replace into task_result values(#{taskID},#{projectID},#{ip},#{ipInfo})")
    void saveDeepScanResultResult(@Param("taskID") int taskID, @Param("projectID") int projectID, @Param("ip") String ip, @Param("ipInfo") String ipInfo);
}
