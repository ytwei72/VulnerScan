package com.dky.vulnerscan.dao;

import com.dky.vulnerscan.entityview.Banner;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by bo on 2017/4/7.
 */
@Repository
@Mapper
public interface UserBannerDao {

    @Insert("insert into user_banner(protocol,port,encoding,type,file_name,add_time) values(#{banner.protocol},#{banner.port},#{banner.encoding},#{banner.type},#{banner.fileName},#{banner.addTime})")
    int addBanner(@Param("banner") Banner banner);

    @Select("select id,protocol,port,encoding,type,file_name as fileName,add_time as addTime from user_banner limit #{offset} offset #{begin}")
    List<Banner> getPageBanner(@Param("begin") int begin, @Param("offset") int offset);

    @Select("select count(*) from user_banner")
    int countBanner();

    @Select("select id,protocol,port,encoding,type,file_name as fileName from user_banner where id = #{id}")
    Banner getBannerById(@Param("id") int id);

    @Delete("delete from user_banner where id =#{id}")
    int deleteBannerById(@Param("id") int id);

    @Update("update user_banner set protocol=#{banner.protocol},port=#{banner.port},encoding=#{banner.encoding},type=#{banner.type},file_name=#{banner.fileName} where id = #{banner.id}")
    int updateBanner(@Param("banner") Banner banner);
}
