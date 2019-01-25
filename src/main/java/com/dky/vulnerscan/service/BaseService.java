package com.dky.vulnerscan.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dky.vulnerscan.entity.DeviceType;
import com.dky.vulnerscan.entity.IpInfo;
import com.dky.vulnerscan.entity.OnvifInfo;
import com.dky.vulnerscan.entity.PortInfo;
import com.dky.vulnerscan.entity.TaskResult;
import com.dky.vulnerscan.entity.VulInfo;
import com.dky.vulnerscan.entityview.PageNation;
import com.dky.vulnerscan.entityview.VulCount;
import com.dky.vulnerscan.util.JsonUtil;

@Service
public class BaseService {

    public PageNation getPageNation(int allNum, int currentPage, int perPage) {
        PageNation pageNation = new PageNation();
        pageNation.setAllNum(allNum);
        pageNation.setCurrentPage(currentPage);
        pageNation.setPerPage(perPage);
        return pageNation;
    }

    //对任务执行结果序列化保存内内存，key值为ip
    public HashMap<String, IpInfo> serializeTaskResult(List<TaskResult> taskResultList) {
        HashMap<String, IpInfo> taskResultMap = new HashMap<>();
        for (int i = 0; i < taskResultList.size(); i++) {
            TaskResult taskResult = taskResultList.get(i);
            IpInfo ipInfo = (IpInfo) JsonUtil.JsonToObj(taskResult.getIpInfo(), IpInfo.class);
            taskResultMap.put(taskResult.getIpAddr(), ipInfo);
        }
        return taskResultMap;
    }

    public String getDeviceType(String deviceType) {
        if (deviceType.equals("IP Camera")) {
            return "摄像机";
        }
        if (deviceType.equals("DVR") || deviceType.equals("NVR")) {
            return "NVR/DVR";
        }
        return "其它";
    }

    //从设备类型的识别结果中找出相似度最大的作为设备类型
    public DeviceType getMaxSimilarityDevType(ArrayList<DeviceType> deviceTypeList) {
        int similarity = 0;
        DeviceType deviceType = new DeviceType();
        for (int i = 0; i < deviceTypeList.size(); i++) {
            deviceType = deviceTypeList.get(i);
            if (deviceType.getSimilarity() > similarity) {
                similarity = deviceType.getSimilarity();
            }
        }
        return deviceType;
    }

    //统计不同漏洞等级数量
    public VulCount countVulLevelNum(VulCount vulCount, ArrayList<VulInfo> vulList) {
        int allNum = 0, extremeHighNum = 0, highNum = 0, midNum = 0, lowNum = 0;
        for (int i = 0; i < vulList.size(); i++) {
            if (vulList.get(i).getVulLevel() != null) {
                allNum++;
                String level = vulList.get(i).getVulLevel();
                switch (level) {
                    case "严重":
                        extremeHighNum++;
                        break;
                    case "高危":
                        highNum++;
                        break;
                    case "中危":
                        midNum++;
                        break;
                    case "低危":
                        lowNum++;
                        break;
                }
            }
        }
        vulCount.setAllNum(vulCount.getAllNum() + allNum);
        vulCount.setExtremeHigh(vulCount.getExtremeHigh() + extremeHighNum);
        vulCount.setHigh(vulCount.getHigh() + highNum);
        vulCount.setMid(vulCount.getMid() + midNum);
        vulCount.setLow(vulCount.getLow() + lowNum);
        return vulCount;
    }

    //统计每个设备的漏洞信息
    public VulCount countDevVul(ArrayList<VulInfo> onvifVulList, ArrayList<PortInfo> portInfoList) {
        VulCount vulDevCount = new VulCount();
        countVulLevelNum(vulDevCount, onvifVulList);// 统计onvif漏洞
        for (int i = 0; i < portInfoList.size(); i++) { // 统计每个端口对应的漏洞
            if (portInfoList.get(i).getVulList() != null) {
                countVulLevelNum(vulDevCount, portInfoList.get(i).getVulList());
            }
        }
        return vulDevCount;
    }

    //计数
    public void countNum(HashMap<String, Integer> map, String key) {
        if (map.containsKey(key)) {
            map.put(key, map.get(key) + 1);
        } else {
            map.put(key, 1);
        }
    }

    //统计任务执行结果漏洞信息
    public void countTaskResultVulInfo(ArrayList<VulInfo> vulInfoList, HashMap<String, Integer> vulTypeMap,
                                       HashMap<String, Integer> vulLevelMap, HashMap<String, Integer> vulAffectMap) {
        for (int i = 0; i < vulInfoList.size(); i++) {
            if (vulInfoList.get(i).getVulType() != null) {
                countNum(vulLevelMap, vulInfoList.get(i).getVulLevel());
                countNum(vulTypeMap, vulInfoList.get(i).getVulType());
                countNum(vulAffectMap, vulInfoList.get(i).getVulAffect());
            }
        }
    }


    //把所有的null值改为空字符串
    public VulInfo changeNullToEmptyString(VulInfo vulInfo) {
        if (vulInfo.getVulLevel() == null) {
            vulInfo.setVulLevel("");
        }
        if (vulInfo.getVulTitle() == null) {
            vulInfo.setVulTitle("");
        }
        if (vulInfo.getVulType() == null) {
            vulInfo.setVulType("");
        }
        if (vulInfo.getVulAffect() == null) {
            vulInfo.setVulAffect("");
        }
        if (vulInfo.getVulSolution() == null) {
            vulInfo.setVulSolution("");
        }
        return vulInfo;
    }

    //统计每个设备的服务数量
    public int countNumService(OnvifInfo onvifInfo, ArrayList<PortInfo> portInfoList) {
        int serviceCount = 0;
        for (int i = 0; i < portInfoList.size(); i++) {
            String serviceType = portInfoList.get(i).getService().getType();
            if (!serviceType.equals("") && !serviceType.equals("unknown")) {
                serviceCount++;
            }
        }
        if (onvifInfo.getPort() != 0) {
            serviceCount++;
        }
        return serviceCount;
    }

}
