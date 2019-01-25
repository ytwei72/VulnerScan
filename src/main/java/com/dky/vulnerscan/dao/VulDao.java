package com.dky.vulnerscan.dao;

import java.util.List;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Repository;

import com.dky.vulnerscan.entity.VulInfo;


@Repository
@Mapper
public interface VulDao {
    // 加载所有的漏洞及其所属的类型
    @Results(value = {
            @Result(id = true, property = "id", column = "vulid", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "vulLevel", column = "risk_level", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "vulType", column = "tech_stype", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "vulTitle", column = "title", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "vulAffect", column = "affect_type", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "vulSolution", column = "solution", javaType = String.class, jdbcType = JdbcType.VARCHAR)})
    @Select("select distinct(vulid),risk_level,tech_stype,title,affect_type,solution from iie_vul_info")
    List<VulInfo> loadSystemVulInfo();

    //从用户漏洞库中加载漏洞信息
    @Results(value = {
            @Result(id = true, property = "id", column = "vul_id", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "vulLevel", column = "risklevel", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "vulType", column = "vul_type", javaType = String.class, jdbcType = JdbcType.VARCHAR)})
    @Select("select distinct(vul_id),risklevel,vul_type from vulnerability")
    List<VulInfo> loadUserVulInfo();

    //根据漏洞标号加载漏洞信息
    @Select("select vulid,risk_level,title,description,solution from iie_vul_info where vulid=#{vulid}")
    @Results(value = {
            @Result(id = true, property = "id", column = "vulid", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "vulLevel", column = "risk_level", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "vulTitle", column = "title", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "msg", column = "description", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "vulSolution", column = "solution", javaType = String.class, jdbcType = JdbcType.VARCHAR)})
    VulInfo getVulInfoByID(@Param("vulid") String vulId);
}