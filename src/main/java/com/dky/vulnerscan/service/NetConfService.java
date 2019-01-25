package com.dky.vulnerscan.service;

import com.dky.vulnerscan.entity.Network;
import com.dky.vulnerscan.util.NetConfiger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dky.vulnerscan.dao.NetConfDao;

@Service
public class NetConfService {
    @Autowired
    private NetConfDao netConfDao;

    //获取eth0的网络配置信息
    public Network getEth0NetConfigure(String ethName) {
        NetConfiger comNetConfiger = new NetConfiger(ethName);
        comNetConfiger.load();
        Network network = new Network(comNetConfiger.device, comNetConfiger.IP, comNetConfiger.netmask,
                comNetConfiger.gateway, comNetConfiger.DNS1, comNetConfiger.DNS2, comNetConfiger.dhcpFlag, getEth0Bandwidth());
        return network;
    }

    //修改网络的配置信息
    //修改网络的配置信息
    public boolean changeConfig(Network network) {
        //进行网络配置
        NetConfiger comNetConfiger = new NetConfiger(network.getEthName());
        comNetConfiger.IP = network.getIp();
        comNetConfiger.netmask = network.getNetmask();
        comNetConfiger.gateway = network.getGateway();
        comNetConfiger.DNS1 = network.getDns1();
        comNetConfiger.DNS2 = network.getDns2();
        comNetConfiger.dhcpFlag = network.getDhcpFlag();
        boolean res = comNetConfiger.trySet();

        if (res == true) {
            //如果设置成功则更新数据库
            netConfDao.updateEth0NetConf(network);
        }
        return res;
    }

    //获取eth0的带宽
    public int getEth0Bandwidth() {
        return netConfDao.getEth0BrandWidth();
    }
}
