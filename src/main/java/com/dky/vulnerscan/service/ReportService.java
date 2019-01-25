package com.dky.vulnerscan.service;

import com.dky.vulnerscan.dao.ProjectDao;
import com.dky.vulnerscan.dao.TaskResultDao;
import com.dky.vulnerscan.entity.IpInfo;
import com.dky.vulnerscan.entity.Project;
import com.dky.vulnerscan.entity.TaskResult;
import com.dky.vulnerscan.entity.VulInfo;
import com.dky.vulnerscan.entityview.*;
import com.dky.vulnerscan.util.VulUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Date;
import java.util.HashMap;
/**
 * FileName ReportService.java
 * 用于生成报表所需要的数据
 * Created by liushaoshuai on 2016/12/15.
 */
@Service
public class ReportService extends BaseService {
    @Autowired
    private TaskResultDao taskResultDao;
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    TaskService taskService;
    @Autowired
    TaskResultService taskResultService;
    public ReportResultCount getReportResultCount(int projectId, int taskId) {//统计本次扫描结果
        ReportResultCount reportResultCount = new ReportResultCount();
        getReportCover(projectId,taskId,reportResultCount);
        // 从数据库查询此次任务的结果到内存并完成序列化
        List<TaskResult> taskResultList = taskResultDao.getTaskResult(taskId, projectId);
        HashMap<String, IpInfo> taskResultMap = super.serializeTaskResult(taskResultList);
        HashMap<String, Integer> brandMap = new HashMap<String, Integer>(); //工控设备的品牌分布
        int checkedVulNum = 0;
        for (String ipAddr : taskResultMap.keySet()) {
            IpInfo ipInfo = taskResultMap.get(ipAddr);
            VulUtil vulUtil = new VulUtil(ipInfo);
            if (vulUtil.getVulInfoLists() != null && vulUtil.getVulInfoLists().size() > 0 && VulUtil.countVulInfos(vulUtil.getVulInfoLists()) > 0) {
                reportResultCount.setVulIpNum(reportResultCount.getVulIpNum() + 1);  //存在漏洞的主机数
                reportResultCount.setVulNum(reportResultCount.getVulNum() + VulUtil.countVulInfos(vulUtil.getVulInfoLists())); //漏洞数量
                checkedVulNum += vulUtil.countCheckedVulNum(vulUtil.getVulInfoLists()); //统计已验证的漏洞数量
            }
            if (vulUtil.getLocalVulInfo() != null && vulUtil.getLocalVulInfo().size() > 0 && VulUtil.countVulInfos(vulUtil.getLocalVulInfo()) > 0) {
                reportResultCount.setLocalVulNum(reportResultCount.getLocalVulNum() + VulUtil.countVulInfos(vulUtil.getLocalVulInfo()));//本地库漏洞数量
            }
            if (vulUtil.getExploitVulInfo() != null && vulUtil.getExploitVulInfo().size() > 0 && VulUtil.countVulInfos(vulUtil.getExploitVulInfo()) > 0) {
                reportResultCount.setExploitVulNum((reportResultCount.getExploitVulNum() + VulUtil.countVulInfos(vulUtil.getExploitVulInfo())));//Exploit库漏洞数量
            }
            if (vulUtil.getOpenvasVulInfo() != null && vulUtil.getOpenvasVulInfo().size() > 0 && VulUtil.countVulInfos(vulUtil.getOpenvasVulInfo()) > 0) {
                reportResultCount.setOpenVasVulNum(reportResultCount.getOpenVasVulNum() + VulUtil.countVulInfos(vulUtil.getOpenvasVulInfo()));//OpenVas库漏洞数量
            }
            if (ipInfo.getDeviceInfoSummary() != null) {//统计工控服务列表
                String devType = super.getDeviceType(ipInfo.getDeviceInfoSummary().getSecondDeviceType());
                if (devType.equals("摄像机") || devType.equals("NVR/DVR")) {
                    reportResultCount.setServiceList(mapToArrayList(vulUtil.countServlist(vulUtil.getVulInfoLists())));
                }
            }
            if (ipInfo.getDeviceInfoSummary() != null) {// 设备类型、品牌的统计
                String devType = super.getDeviceType(ipInfo.getDeviceInfoSummary().getSecondDeviceType());
                if (devType.equals("摄像机") || devType.equals("NVR/DVR")) {
                    countNum(brandMap, super.getMaxSimilarityDevType(ipInfo.getDeviceInfoSummary().getDeviceInfoList()).getBrand());
                    if (vulUtil.getVulInfoLists() != null && vulUtil.getVulInfoLists().size() > 0 && VulUtil.countVulInfos(vulUtil.getVulInfoLists()) > 0) {
                        reportResultCount.setInControlDeviceVulNum(reportResultCount.getInControlDeviceVulNum() + 1);//存在漏洞的工控设备数量
                    }
                }
            }
        }
        reportResultCount.setCheckedVulNum(checkedVulNum);
        reportResultCount.setInControlDeviceList(mapToArrayList(brandMap));
        return reportResultCount;
    }

    private void getReportCover(int projectId, int taskId, ReportResultCount reportResultCount){
        TaskOverView taskOverView = taskService.getTaskOverView(taskId, projectId);
        TaskResultCount taskResultCount = taskResultService.getTaskResultCount(taskOverView,
                taskId, projectId);
        Project project = projectDao.getProjectByID(projectId);
        reportResultCount.setProjectName(project.getProjectName()); //报表的项目名
        reportResultCount.setCherker(project.getChecker());         //报表的检查员
        reportResultCount.setTaskNo(taskOverView.getCounts()+"");   //报表的任务编号
        reportResultCount.setCheckerDate(new SimpleDateFormat("yyy.MM.dd").format(new Date()).toString());//检查日期
        reportResultCount.setAliveIpNum(taskOverView.getDeviceNum()); //存活主机数
        reportResultCount.setInControlDeviceNum(taskOverView.getVideoNum()); //工控设备数
        reportResultCount.setOtherDeviceNum(taskOverView.getDeviceNum()-taskOverView.getVideoNum());//其它设备数
        ArrayList<Param> vulStyleList=taskResultCount.getVulStyleList();
        ArrayList<HashMap<String,String>> vulstyle=new ArrayList<HashMap<String,String>>();
        if(vulStyleList!=null && vulStyleList.size()>0){
            for(int i=0;i<vulStyleList.size();i++){
                Param param=vulStyleList.get(i);
                HashMap<String,String> map=new HashMap<>();
                map.put("name",param.getName());
                map.put("num",param.getValue()+"");
                vulstyle.add(map);
            }
        }
        reportResultCount.setVulTypeList(vulstyle);//漏洞类型分布
    }
    //统计各设备的详情信息
    public ArrayList<ReportDeviceInfo> getReportDevInfos(int projectId, int taskId) {
        List<TaskResult> taskResultList = taskResultDao.getTaskResult(taskId, projectId);
        HashMap<String, IpInfo> taskResultMap = super.serializeTaskResult(taskResultList);
        ArrayList<ReportDeviceInfo> reportDeviceInfos = new ArrayList<ReportDeviceInfo>();  //设备详情
        for (String ipAddr : taskResultMap.keySet()) {
            ReportDeviceInfo reportDeviceInfo = new ReportDeviceInfo();
            reportDeviceInfo.setIp(ipAddr);  //ip
            IpInfo ipInfo = taskResultMap.get(ipAddr);
            if (ipInfo.getDeviceInfoSummary() != null) {// brand
                reportDeviceInfo.setBrand(super.getMaxSimilarityDevType(ipInfo.getDeviceInfoSummary().getDeviceInfoList()).getBrand());
            } else {
                reportDeviceInfo.setBrand("其它");
            }
            if (ipInfo.getOs() != null) {
                reportDeviceInfo.setOs(ipInfo.getOs()); //os
            }else{
                reportDeviceInfo.setOs("无");
            }
            VulUtil vulUtil = new VulUtil(ipInfo);
            if (vulUtil.getVulInfoLists() != null && vulUtil.getVulInfoLists().size() > 0) {
                reportDeviceInfo.setReportVulInfos(vulUtil.getVulInfoLists());
            }
            if(vulUtil.getLocalVulInfo()!=null && vulUtil.getLocalVulInfo().size()>0 ){
                reportDeviceInfo.setLocalVulInfos(vulUtil.getLocalVulInfo());
            }
            if(vulUtil.getExploitVulInfo()!=null && vulUtil.getExploitVulInfo().size()>0){
                reportDeviceInfo.setExploitVulInfos(vulUtil.getExploitVulInfo());
            }
            if(vulUtil.getOpenvasVulInfo()!=null && vulUtil.getOpenvasVulInfo().size()>0){
                reportDeviceInfo.setOpenvasVulInfos(vulUtil.getOpenvasVulInfo());
            }
            reportDeviceInfos.add(reportDeviceInfo);
        }
        return reportDeviceInfos;
    }
    //统计漏洞详情信息
    public  ArrayList<ReportVulDetailInfo> getVulDetailInfo(ArrayList<ReportDeviceInfo> reportDeviceInfos,String type) {
        ArrayList<ReportVulDetailInfo> reportVulDetailInfos = new ArrayList<>();
        HashMap<String, String> locVulNameMap = new HashMap<String, String>();
        ArrayList<HashMap<String,String>> ips=new ArrayList<>();
        if (reportDeviceInfos!=null && reportDeviceInfos.size() > 0) {
            for (int i = 0; i < reportDeviceInfos.size(); i++) {
                ArrayList<ReportVulInfo> rVulInfos=new ArrayList<>();
                if(type.equals("本地漏洞库")){
                    rVulInfos = reportDeviceInfos.get(i).getLocalVulInfos();
                }
                if(type.equals("Exploit")){
                    rVulInfos = reportDeviceInfos.get(i).getExploitVulInfos();
                }
                if(type.equals("openVAS")){
                    rVulInfos = reportDeviceInfos.get(i).getOpenvasVulInfos();
                }
                String ip = reportDeviceInfos.get(i).getIp();
                if (rVulInfos!=null && rVulInfos.size() > 0) {
                    for (int j = 0; j < rVulInfos.size(); j++) {
                        ArrayList<VulInfo> vulInfos = rVulInfos.get(j).getVulInfos();
                        if (vulInfos!=null && vulInfos.size() > 0) {
                            for (int k = 0; k < vulInfos.size(); k++) {
                                if (vulInfos.get(k).getVulTitle()==null || vulInfos.get(k).getVulTitle().length() < 0) {
                                    continue;
                                }
                                String vulName = vulInfos.get(k).getVulTitle();
                                String vullevel = "N/A";
                                String vulVerifyState = "N/A";
                                if (vulInfos.get(k).getVulLevel()!=null && vulInfos.get(k).getVulLevel().length() > 0) {
                                    vullevel = vulInfos.get(k).getVulLevel();
                                }
                                if (vulInfos.get(k).getVerifyState()!=null && vulInfos.get(k).getVerifyState().length() > 0) {
                                    vulVerifyState = vulInfos.get(k).getVerifyState();
                                }
                                locVulNameMap.put(vulName, vullevel);
                                HashMap<String,String> ipmap=new HashMap<>();
                                ipmap.put("ip",ip);
                                ipmap.put("vulName",vulName);
                                ipmap.put("vulLevel",vullevel);
                                ipmap.put("vulVerifyState",vulVerifyState);
                                ips.add(ipmap);
                            }
                        }

                    }
                }

            }
        }
        Set<String> vulNameSet=locVulNameMap.keySet();
        for(String vulName:vulNameSet){
            ReportVulDetailInfo reportVulDetailInfo=new ReportVulDetailInfo();
            reportVulDetailInfo.setVulname(vulName);
            reportVulDetailInfo.setVullevel(locVulNameMap.get(vulName));
            ArrayList<HashMap<String,String>> ipArray=new ArrayList<>();
            if(ips!=null && ips.size()>0){
                for(int i=0;i<ips.size();i++){
                    HashMap<String,String> map=ips.get(i);
                    if(locVulNameMap.containsKey(map.get("vulName"))){
                        HashMap<String,String> ip=new HashMap<>();
                        ip.put("ip",map.get("ip"));
                        ip.put("vulVerifyState",map.get("vulVerifyState"));
                        ipArray.add(ip);
                    }
                }
            }
            reportVulDetailInfo.setIps(ipArray);
            reportVulDetailInfos.add(reportVulDetailInfo);
        }
        return reportVulDetailInfos;
    }

    //将map转为List工具函数
    private ArrayList<HashMap<String, String>> mapToArrayList(HashMap<String, Integer> map) {
        Set<String> setMapName = map.keySet();
        ArrayList<HashMap<String, String>> tempArrayList = new ArrayList<HashMap<String, String>>();
        for (String name : setMapName) {
            HashMap<String, String> vmap = new HashMap<String, String>();
            vmap.put("name", name);
            vmap.put("num", map.get(name) + "");
            tempArrayList.add(vmap);
        }
        return tempArrayList;
    }
}

