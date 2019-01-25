package com.dky.vulnerscan.dao;

import com.dky.vulnerscan.entity.ReceiverEmail;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
@Mapper
public interface EmailDao {
    @Results(value = {
            @Result(id = true, property = "emailID", column = "email_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "userName", column = "username", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "receiverName", column = "to_emails_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "receiverEmailAddress", column = "to_emails_address", javaType = String.class, jdbcType = JdbcType.VARCHAR)})
    @Select("select * from to_email where username=#{userName}")
    ArrayList<ReceiverEmail> getUserSendEmails(@Param("userName") String userName);

    //判断是否已经存在相同的邮箱地址
    @Select("select count(*) from to_email where to_emails_address=#{receiverEmailAddress}")
    int hasAlreadyExitsEmailAddress(@Param("receiverEmailAddress") String receiverEmailAddress);

    //添加一个用户下的邮件收件人
    @Insert("insert into to_email(username,to_emails_name,to_emails_address) value(#{userName},#{receiverName},#{receiverEmailAddress})")
    void addEmailAddress(@Param("userName") String userName, @Param("receiverName") String receiverName, @Param("receiverEmailAddress") String receiverEmailAddress);

    //根据用户名删除该用户所有邮件收件人
    @Delete("delete from to_email where username=#{userName}")
    void deleteToEmailByuserName(@Param("userName") String userName);

    //根据用户名和收件人邮箱删除该邮件收件人
    @Delete("delete from to_email where username=#{userName} and to_emails_address=#{receiverEmailAddress}")
    void deleteToEmail(@Param("userName") String userName, @Param("receiverEmailAddress") String receiverEmailAddress);
}
