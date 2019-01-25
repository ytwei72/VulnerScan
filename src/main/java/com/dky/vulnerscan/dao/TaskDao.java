package com.dky.vulnerscan.dao;

import java.util.List;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Repository;

import com.dky.vulnerscan.entity.Task;

@Repository
@Mapper
public interface TaskDao {
    //获取所有的任务数
    @Select("select count(*) from task_info")
    int getAllTaskNum();

    //删除项目对应的所有任务
    @Delete("delete from task_info where project_id=#{projectID}")
    void deleteProjectTask(@Param("projectID") int projectID);

    //获取指定项目对应的所有任务
    @Results(value = {
            @Result(id = true, property = "taskID", column = "task_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "projectID", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "checker", column = "checker", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "totalProgress", column = "total_progress", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "hdProgress", column = "hd_progress", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "sdProgress", column = "sd_progress", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "odProgress", column = "od_progress", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "drProgress", column = "dr_progress", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "wpProgress", column = "wp_progress", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "vsProgress", column = "vs_progress", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "state", column = "state", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "iscancel", column = "iscancel", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "compeletedFlag", column = "compeleted_flag", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "startTime", column = "start_time", javaType = Long.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "takingTime", column = "taking_time", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "taskError", column = "task_error", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "reportUrl", column = "report_url", javaType = String.class, jdbcType = JdbcType.VARCHAR)})
    @Select("select * from task_info where project_id=#{projectID}")
    List<Task> getProjectTaskList(@Param("projectID") int projectID);

    //获取项目对应的指定页的任务
    @Results(value = {
            @Result(id = true, property = "taskID", column = "task_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "projectID", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "checker", column = "checker", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "totalProgress", column = "total_progress", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "hdProgress", column = "hd_progress", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "sdProgress", column = "sd_progress", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "odProgress", column = "od_progress", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "drProgress", column = "dr_progress", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "wpProgress", column = "wp_progress", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "vsProgress", column = "vs_progress", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "state", column = "state", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "iscancel", column = "iscancel", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "compeletedFlag", column = "compeleted_flag", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "startTime", column = "start_time", javaType = Long.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "takingTime", column = "taking_time", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "taskError", column = "task_error", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "reportUrl", column = "report_url", javaType = String.class, jdbcType = JdbcType.VARCHAR)})
    @Select("select * from task_info where project_id=#{projectID} order by start_time desc limit #{offset} offset #{begin}")
    List<Task> getPageProjectTaskList(@Param("projectID") int projectID, @Param("begin") int begin, @Param("offset") int offset);

    //检测任务队列中是否有此项目对应的任务正在排队
    @Select("select count(*) from task_info where project_id=#{projectID} and compeleted_flag=0")
    int getProjectUnfinTaskNum(@Param("projectID") int projectID);

    // 获取任务表中第一个排队任务
    @Results(value = {
            @Result(id = true, property = "taskID", column = "task_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "projectID", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER)})
    @Select("select task_id,project_id from task_info where compeleted_flag=0 ORDER BY task_id asc limit 1")
    Task getFirstUnfinTask();

    // 更新任务执行状态
    @Update("update task_info set state=#{state} where task_id=#{taskID}")
    void updateTaskState(@Param("taskID") int taskID, @Param("state") String state);

    // 更新任务错误信息
    @Update("update task_info set task_error=#{error} where task_id=#{taskID} and project_id=#{projectID}")
    void updateTaskError(@Param("taskID") int taskid, @Param("projectID") int projectid, @Param("error") String error);

    // 更新任务的进度
    @Update("update task_info set ${process}=${progress} where task_id=${taskID}")
    @Options(statementType = StatementType.STATEMENT)
    void updateTaskProgress(@Param("taskID") int taskid, @Param("progress") int progress, @Param("process") String process);

    // 记录任务开始执行的时间
    @Update("update task_info set start_time=#{startTime} where task_id=#{taskID}")
    void recordStartTime(@Param("taskID") int taskid, @Param("startTime") long start_time);

    // 更新正在执行任务的耗时
    @Update("update task_info set taking_time=#{takingTime} where task_id=#{taskID}")
    void updateTaskTakingTime(@Param("taskID") int taskid, @Param("takingTime") long takingTime);

    // 更新任务是否执行完成标识
    @Update("update task_info set compeleted_flag=1 where task_id=#{taskID}")
    void setTaskEndFlag(@Param("taskID") int taskID);

    // 获取任务对应的状态
    @Select("select state from task_info where task_id=#{taskID}")
    String getTaskState(@Param("taskID") int taskid);

    // 在任务表中插入一个新任务
    @Insert("insert into task_info(project_id,checker,compeleted_flag,state,start_time) values(#{task.projectID},#{task.checker},#{task.compeletedFlag},#{task.state},#{task.startTime})")
    void addTask(@Param("task") Task task);

    // 获取项目的最近一次任务的任务编号
    @Results(value = {
            @Result(id = true, property = "taskID", column = "task_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "projectID", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "checker", column = "checker", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "totalProgress", column = "total_progress", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "state", column = "state", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "startTime", column = "start_time", javaType = Long.class, jdbcType = JdbcType.BIGINT)})
    @Select("select task_id,project_id,state,total_progress,start_time from task_info where project_id=#{projectID} order by start_time desc limit 1")
    Task getFinalTask(@Param("projectID") int projectID);

    // 根据任务编号获取指定任务
    @Results(value = {
            @Result(id = true, property = "taskID", column = "task_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "projectID", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "checker", column = "checker", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "totalProgress", column = "total_progress", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "hdProgress", column = "hd_progress", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "sdProgress", column = "sd_progress", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "odProgress", column = "od_progress", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "drProgress", column = "dr_progress", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "wpProgress", column = "wp_progress", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "vsProgress", column = "vs_progress", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "state", column = "state", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "iscancel", column = "iscancel", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "compeletedFlag", column = "compeleted_flag", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "startTime", column = "start_time", javaType = Long.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "takingTime", column = "taking_time", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "taskError", column = "task_error", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "reportUrl", column = "report_url", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "usedFlow", column = "used_flow", javaType = Double.class, jdbcType = JdbcType.DOUBLE)})
    @Select("select * from task_info where task_id=#{taskID}")
    Task getTaskByTaskID(@Param("taskID") int taskID);

    // 统计项目对应的任务是第几次执行
    @Select("select count(*) from task_info where project_id=#{projectID} and task_id<=#{taskID}")
    int getTaskCounts(@Param("taskID") int taskID, @Param("projectID") int projectID);

    //删除指定的任务
    @Delete("delete from task_info where task_id=#{taskID}")
    void deleteTask(@Param("taskID") int taskID);

    // 获取任务对应的报表URL
    @Select("select report_url from task_info where task_id=#{taskID}")
    String getTaskReportUrl(@Param("taskID") int taskid);

    // 更新任务的报表url
    @Update("update task_info set report_url=#{url} where task_id=#{taskID}")
    void updateTaskReportUrl(@Param("taskID") int taskID, @Param("url") String url);

    //设置手动停止标识
    @Update("update task_info set iscancel=1 where task_id=#{taskID} and project_id=#{projectID}")
    void setTaskCancelFlag(@Param("projectID") int projectID, @Param("taskID") int taskID);

    //获取指定项目对应的任务数量
    @Select("select count(*) from task_info where project_id=#{projectID}")
    int getProjectTaskCount(@Param("projectID") int projectID);

    //更新任务执行消耗的流量
    @Update("update task_info set used_flow=#{usedFlow} where task_id=#{taskID}")
    void updateTaskUsedFlow(@Param("taskID") int taskID, @Param("usedFlow") double curRate);

    //查找指定检查员对应的任务及任务对应的报表
    @Results(value = {
            @Result(id = true, property = "taskID", column = "task_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "reportUrl", column = "report_url", javaType = String.class, jdbcType = JdbcType.VARCHAR)})
    @Select("select task_id,report_url from task_info where checker=#{checkerName}")
    List<Task> getCheckerTaskList(@Param("checkerName") String checkerName);

    //删除检查员对应的任务
    @Delete("delete from task_info where checker=#{checkerName}")
    void deleteCheckerTask(@Param("checkerName") String checkerName);

    @Update("update task_info set state = '异常结束',compeleted_flag = 1 where compeleted_flag = 0")
    void endUnfinishedTask();
}
