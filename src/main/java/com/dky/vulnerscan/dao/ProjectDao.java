package com.dky.vulnerscan.dao;

import java.util.List;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Repository;

import com.dky.vulnerscan.entity.Project;

@Repository
@Mapper
public interface ProjectDao {
    //获取所有的项目个数
    @Select("select count(project_id) from project_info")
    int getAllProjectNum();

    // 获取指定页数的项目
    @Results(value = {
            @Result(id = true, property = "projectID", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "checker", column = "checker", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "describe", column = "describle", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "projectFlag", column = "project_flag", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "createTime", column = "create_time", javaType = Long.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "state", column = "state", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "times", column = "task_num", javaType = Integer.class, jdbcType = JdbcType.INTEGER)})
    @Select("select project_id,project_name,checker,describle,project_flag,create_time,state,task_num from project_info where checker like #{userName} order by create_time desc limit #{offset} offset #{begin}")
    List<Project> getPageProjectList(@Param("begin") int begin, @Param("offset") int offset, @Param("userName") String userName);

    // 删除指定项目
    @Delete("delete from project_info where project_id=#{projectID}")
    void deleteProject(@Param("projectID") int projectID);

    // 获取项目表中的周期性项目
    @Results(value = {
            @Result(id = true, property = "projectID", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "time", column = "time", javaType = Long.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "nextTime", column = "next_time", javaType = Long.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "space", column = "space", javaType = Integer.class, jdbcType = JdbcType.INTEGER)})
    @Select("select project_id,time,space,next_time from project_info where project_flag=1")
    List<Project> getPeriodicProject();

    // 通过项目编号，查询指定项目
    @Results(value = {
            @Result(id = true, property = "projectID", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "checker", column = "checker", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "describe", column = "describle", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "target", column = "target", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "hdAdditionPort", column = "hd_addition_port", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "intensity", column = "intensity", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "protocol", column = "protocol", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "blackList", column = "blacklist", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "enableTopoProbe", column = "enable_topo_probe", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "scanType", column = "scan_type", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "sdAdditionPortTcp", column = "sd_addition_port_tcp", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "sdAdditionPortUdp", column = "sd_addition_port_udp", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "topPorts", column = "top_ports", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "excludePorts", column = "exclude_ports", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "enableVersionDetec", column = "enable_version_detec", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "versionIntensity", column = "version_intensity", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "enableOsDetec", column = "enable_os_detec", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "probeModule", column = "probe_module", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "enableReboot", column = "enable_reboot", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "enableChange", column = "enable_change", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "projectFlag", column = "project_flag", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "space", column = "space", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "createTime", column = "create_time", javaType = Long.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "time", column = "time", javaType = Long.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "nextTime", column = "next_time", javaType = Long.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "state", column = "state", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "times", column = "task_num", javaType = Integer.class, jdbcType = JdbcType.INTEGER)})
    @Select("select * from project_info where project_id=#{projectID}")
    Project getProjectByID(@Param("projectID") int projectID);

    // 更新项目下次执行时间
    @Update("update project_info set next_time=#{nextTime} where project_id=#{projectID}")
    void updateNextTime(@Param("nextTime") long nextTime, @Param("projectID") int projectID);

    //获取项目对应的所有任务数
    @Select("select count(*) from task_info where project_id=#{projectID}")
    int getProjectTaskNum(@Param("projectID") int projectID);

    // 新建一个项目，保存到项目表中,并返回自增的主键
    @Insert("insert into project_info(project_name,checker,describle,target,hd_addition_port,intensity,protocol,blacklist,enable_topo_probe,scan_type,sd_addition_port_tcp,sd_addition_port_udp,top_ports," +
            "exclude_ports,enable_version_detec,version_intensity,enable_os_detec,probe_module,enable_reboot,enable_change,project_flag,space,create_time,time,state,next_time) " +
            "values(#{project.projectName},#{project.checker},#{project.describe},#{project.target},#{project.hdAdditionPort},#{project.intensity}," +
            "#{project.protocol},#{project.blackList},#{project.enableTopoProbe},#{project.scanType},#{project.sdAdditionPortTcp},#{project.sdAdditionPortUdp},#{project.topPorts}," +
            "#{project.excludePorts},#{project.enableVersionDetec},#{project.versionIntensity},#{project.enableOsDetec},#{project.probeModule},#{project.enableReboot}," +
            "#{project.enableChange},#{project.projectFlag},#{project.space},#{project.createTime},#{project.time},#{project.state},#{project.nextTime})")
    @Options(useGeneratedKeys = true, keyProperty = "project.projectID", keyColumn = "GENERATED_KEY")
    int addProject(@Param("project") Project project);

    //判断是否已经有项目名存在
    @Select("select count(*) from project_info where project_name=#{projectName}")
    int checkProjectAlreadyExist(@Param("projectName") String projectName);

    //更新项目的状态
    @Update("update project_info set state=#{state} where project_id=#{projectID}")
    void updateProjectState(@Param("projectID") int projectID, @Param("state") String state);

    //项目的任务数量加1
    @Update("update project_info set task_num=task_num+1 where project_id=#{projectID}")
    void plusProjectTaskNum(@Param("projectID")int projectID);

    //项目任务数量减1
    @Update("update project_info set task_num=task_num-1 where project_id=#{projectID}")
    void reduceProjectTaskNum(@Param("projectID") int projectID);

    //删除检查员对应的项目
    @Delete("delete from project_info where checker=#{checkerName}")
    void deleteCheckerProject(@Param("checkerName") String checkerName);

    @Update("update project_info set state = '空闲' ")
    void endUnfinishedProject();
}
