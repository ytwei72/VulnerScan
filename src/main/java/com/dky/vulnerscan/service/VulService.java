package com.dky.vulnerscan.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dky.vulnerscan.dao.VulDao;
import com.dky.vulnerscan.entity.VulInfo;

@Service
public class VulService {
    @Autowired
    private VulDao vulDao;

    //从系统漏洞库中加载漏洞信息
    public HashMap<String, VulInfo> getSystemVulInfo() {
        HashMap<String, VulInfo> systemVulInfoMap = new HashMap<>();
        List<VulInfo> vulInfoList = vulDao.loadSystemVulInfo();
        if (vulInfoList != null) {
            for (VulInfo vulInfo : vulInfoList) {
                systemVulInfoMap.put(vulInfo.getId(), vulInfo);
            }
        }
        return systemVulInfoMap;
    }

    //从用户漏洞库中加载漏洞信息
    public HashMap<String, VulInfo> getUserVulInfo() {
        HashMap<String, VulInfo> userVulInfoMap = new HashMap<>();
        List<VulInfo> vulInfoList = vulDao.loadUserVulInfo();
        if (vulInfoList != null) {
            for (VulInfo vulInfo : vulInfoList) {
                vulInfo.setVulSolution("");
                vulInfo.setVulAffect("");
                vulInfo.setVulTitle("");
                userVulInfoMap.put(vulInfo.getId(), vulInfo);
            }
        }
        return userVulInfoMap;
    }

    //根据vulID加载漏洞信息
    public VulInfo getVulinfo(String id) {
        return vulDao.getVulInfoByID(id);
    }
}
