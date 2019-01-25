package com.dky.vulnerscan.dao;

import com.dky.vulnerscan.entity.User;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
@Mapper
public interface UserDao {

    //根据登录用户的类型、用户名等查找是否存在此用户
    @Results(value = {
            @Result(id = true, property = "userId", column = "user_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "userName", column = "user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "userType", column = "user_type", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "realName", column = "real_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "addTime", column = "add_time", javaType = Long.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "leftTryNum", column = "left_try_num", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "lastLoginTime", column = "last_login_time", javaType = Long.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "enableManageVulLib", column = "is_add_vul", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "emailName", column = "email_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "emailSubject", column = "email_subject", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "emailContent", column = "email_content", javaType = String.class, jdbcType = JdbcType.VARCHAR)})
    @Select("select * from user_info where user_type=#{userType} and user_name=#{userName}")
    User getUserByTypeAndName(@Param("userType") String userType, @Param("userName") String userName);

    //通过用户名查找用户
    @Results(value = {
            @Result(id = true, property = "userId", column = "user_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "userName", column = "user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "userType", column = "user_type", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "realName", column = "real_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "addTime", column = "add_time", javaType = Long.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "leftTryNum", column = "left_try_num", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "lastLoginTime", column = "last_login_time", javaType = Long.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "enableManageVulLib", column = "is_add_vul", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "emailName", column = "email_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "emailSubject", column = "email_subject", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "emailContent", column = "email_content", javaType = String.class, jdbcType = JdbcType.VARCHAR)})
    @Select("select * from user_info where user_name=#{userName}")
    User getUserByUserName(@Param("userName") String userName);

    //更新用户的登录状态
    @Update("update user_info set left_try_num=#{leftTryTime},last_login_time=#{lastLoginTime} where user_name=#{userName}")
    void updateUserLoginState(@Param("userName") String userName, @Param("leftTryTime") int leftTryNum, @Param("lastLoginTime") long lastLoginTime);

    //修改用户信息
    @Update("update user_info set real_name=#{realName},passwd=#{newPasswd} where user_name=#{userName}")
    void changeUserInfo(@Param("userName") String userName, @Param("realName") String realName, @Param("newPasswd") String newPasswd);

    //获取所有检查员的数目
    @Select("select count(*) from user_info where user_type='checker'")
    int countChecker();

    //删除检查员
    @Delete("delete from user_info where user_name=#{checkerName}")
    void deleteChecker(@Param("checkerName") String checkerName);

    //判断是否已经存在相同姓名的checker
    @Select("select count(*) from user_info where user_name=#{checkerName}")
    int hasAlreadyExitsChecker(@Param("checkerName") String checkerName);

    //添加一个检查员
    @Insert("insert into user_info(user_name,passwd,user_type,real_name,add_time,left_try_num,is_add_vul) value(#{checkerName},#{passwd},#{userType},#{realName},#{addTime},#{leftTryNum},#{addVul})")
    void addChecker(@Param("checkerName") String checkerName, @Param("passwd") String passwd, @Param("userType") String userType, @Param("realName") String realName, @Param("addTime") long addTime, @Param("leftTryNum") int leftTypeNum, @Param("addVul") int addVul);

    @Update("update user_info set is_add_vul=#{addVul} where user_name=#{checkerName}")
    void updateCheckerAddVul(@Param("checkerName") String checkerName, @Param("addVul") int addVul);

    //获取指定页数的检查员
//    @Results(value = {
//            @Result(id = true, property = "userId", column = "user_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
//            @Result(property = "userName", column = "user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
//            @Result(property = "userType", column = "user_type", javaType = String.class, jdbcType = JdbcType.VARCHAR),
//            @Result(property = "realName", column = "real_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
//            @Result(property = "addTime", column = "add_time", javaType = Long.class, jdbcType = JdbcType.BIGINT),
//            @Result(property = "enableManageVulLib", column = "is_add_vul", javaType = Integer.class, jdbcType = JdbcType.INTEGER)})
//    @Select("select user_id,user_name,passwd,real_name,add_time,is_add_vul from user_info where user_type='checker' limit #{offset} offset #{begin}")
//    ArrayList<User> getPageCheckerList(@Param("begin") int begin, @Param("offset") int offset);

    @Results(value = {
            @Result(id = true, property = "userId", column = "user_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "userName", column = "user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "userType", column = "user_type", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "realName", column = "real_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "addTime", column = "add_time", javaType = Long.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "enableManageVulLib", column = "is_add_vul", javaType = Integer.class, jdbcType = JdbcType.INTEGER)})
    @Select("select user_id,user_name,real_name,add_time,is_add_vul from user_info where user_type='checker' limit #{offset} offset #{begin}")
    ArrayList<User> getPageCheckerList(@Param("begin") int begin, @Param("offset") int offset);

    //修改检查员信息
    @Update("update user_info set passwd=#{passwd} where user_name=#{checkerName}")
    void changeChecker(@Param("checkerName") String checkerName, @Param("passwd") String passwd);

    //修改邮箱信息
    @Update("update user_info set email_name=#{sendAddr},email_passwd=#{sendPS},email_content=#{emailContent},email_subject=#{emailSubject}where user_name=#{userName}")
    void changeEmail(@Param("sendAddr") String sendAddr, @Param("sendPS") String sendPS, @Param("emailSubject")String emailSubject,
                            @Param("emailContent")String emailContent, @Param("userName")String userName);
}
