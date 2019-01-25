package com.dky.vulnerscan.dao;

import com.dky.vulnerscan.entity.Vulner;

import java.util.List;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Repository;

/**
 * Created by devicesearch on 2016/12/13.
 */
@Repository
@Mapper
public interface VulnerDao {


    @Select("select * from vulnerability"+"${SQL_extend}")
    @Results(value = {
            @Result(property = "vul_id", column = "vul_id", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "vul_name", column = "vul_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "vul_type", column = "vul_type", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "vul_time", column = "vul_time", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "first_type", column = "first_type", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "affect_brand", column = "affect_brand", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "affect_product", column = "affect_product", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "service", column = "service", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "risklevel", column = "risklevel", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "dangers", column = "dangers", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "description", column = "description", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "poc_filepath", column = "poc_filepath", javaType = String.class, jdbcType = JdbcType.VARCHAR)})
    List<Vulner> getVulnerList(@Param("SQL_extend") String SQL_extend) throws Exception;

    @Select("select count(vul_id) from vulnerability"+"${SQL_extend}")
    int getVulnerCount(@Param("SQL_extend") String SQL_extend) throws Exception;

    @Select("select * from vulnerability where vul_id=#{vul_id}")
    @Results(value = {
            @Result(property = "vul_id", column = "vul_id", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "vul_name", column = "vul_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "vul_type", column = "vul_type", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "vul_time", column = "vul_time", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "first_type", column = "first_type", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "affect_brand", column = "affect_brand", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "affect_product", column = "affect_product", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "service", column = "service", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "risklevel", column = "risklevel", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "dangers", column = "dangers", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "description", column = "description", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "poc_filepath", column = "poc_filepath", javaType = String.class, jdbcType = JdbcType.VARCHAR)})
    Vulner getVulnerbyvulid(@Param("vul_id") String vul_id) throws Exception;

    @Insert("insert into vulnerability values(#{vulner.vul_id},#{vulner.vul_name},#{vulner.vul_type},#{vulner.vul_time}," +
            "#{vulner.first_type},#{vulner.affect_brand},#{vulner.affect_product},#{vulner.service},#{vulner.risklevel}," +
            "#{vulner.dangers},#{vulner.description},#{vulner.poc_filepath})")
    void insertVulner(@Param("vulner") Vulner vulner) throws Exception;

    @Delete("delete from vulnerability where vul_id=#{vul_id}")
    void deleteVulner(@Param("vul_id") String vulid) throws Exception;

    @Update("update vulnerability set vul_id=#{vulner.vul_id},vul_name=#{vulner.vul_name},vul_type=#{vulner.vul_type}," +
            "vul_time=#{vulner.vul_time},first_type=#{vulner.first_type},affect_brand=#{vulner.affect_brand}," +
            "affect_product=#{vulner.affect_product},service=#{vulner.service},risklevel=#{vulner.risklevel}," +
            "dangers=#{vulner.dangers},description=#{vulner.description},poc_filepath=#{vulner.poc_filepath} " +
            "where vul_id=#{vulner.s_value}")
    void updateVulner(@Param("vulner") Vulner vulner) throws Exception;
}
