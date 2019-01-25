package com.dky.vulnerscan.dao;

import com.dky.vulnerscan.entity.Network;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface NetConfDao {
    //获取eth0的带宽
    @Select("select bandwidth from network_info where eth_name='eth0'")
    int getEth0BrandWidth();

    // 更新eth0的网络配置
    @Update("update network_info set ip_addr=#{netConf.ip},subnet_mask=#{netConf.netmask}," +
            "gateway=#{netConf.gateway},bandwidth=#{netConf.bandwidth},dns1=#{netConf.dns1}," +
            "dns2=#{netConf.dns2},dhcp_flag=#{netConf.dhcpFlag} where eth_name='eth0'")
    void updateEth0NetConf(@Param("netConf") Network network);

}
