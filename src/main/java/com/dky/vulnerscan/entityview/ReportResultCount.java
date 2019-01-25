package com.dky.vulnerscan.entityview;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * FileName ReportResultCount.java
 * 用于构造报表页面的展示的各类数据信息
 *
 * @author liushaoshuai on 2016/12/15.
 */

public class ReportResultCount {

    private String projectName;  //项目名字
    private String taskNo;       //任务编号
    private String checkerDate; //检查日期
    private String cherker;     //检查人员
    private int aliveIpNum;  //存活主机数
    private int inControlDeviceNum; //工控设备数量
    private int otherDeviceNum;     //其它设备数量
    private ArrayList<HashMap<String,String>> inControlDeviceList; //工控设备品牌列表
    private int vulNum;//漏洞数量
    private int checkedVulNum;//验证过的漏洞数量
    private int localVulNum;//本地漏洞库数量
    private int exploitVulNum;//exploit漏洞库数量
    private int openVasVulNum; //openVas漏洞库数量
    private int vulIpNum;//存在漏洞的主机数
    private int inControlDeviceVulNum;//存在漏洞的工控设备数量
    private ArrayList<HashMap<String,String>> vulTypeList;//漏洞类型分布
    private ArrayList<HashMap<String,String>> serviceList;//服务分布

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getTaskNo() {
        return taskNo;
    }

    public void setTaskNo(String taskNo) {
        this.taskNo = taskNo;
    }

    public String getCheckerDate() {
        return checkerDate;
    }

    public void setCheckerDate(String checkerDate) {
        this.checkerDate = checkerDate;
    }

    public String getCherker() {
        return cherker;
    }

    public void setCherker(String cherker) {
        this.cherker = cherker;
    }

    public int getAliveIpNum() {
        return aliveIpNum;
    }

    public void setAliveIpNum(int aliveIpNum) {
        this.aliveIpNum = aliveIpNum;
    }

    public int getInControlDeviceNum() {
        return inControlDeviceNum;
    }

    public void setInControlDeviceNum(int inControlDeviceNum) {
        this.inControlDeviceNum = inControlDeviceNum;
    }

    public int getOtherDeviceNum() {
        return otherDeviceNum;
    }

    public void setOtherDeviceNum(int otherDeviceNum) {
        this.otherDeviceNum = otherDeviceNum;
    }

    public ArrayList<HashMap<String, String>> getInControlDeviceList() {
        return inControlDeviceList;
    }

    public void setInControlDeviceList(ArrayList<HashMap<String, String>> inControlDeviceList) {
        this.inControlDeviceList = inControlDeviceList;
    }

    public int getVulNum() {
        return vulNum;
    }

    public void setVulNum(int vulNum) {
        this.vulNum = vulNum;
    }

    public int getCheckedVulNum() {
        return checkedVulNum;
    }

    public void setCheckedVulNum(int checkedVulNum) {
        this.checkedVulNum = checkedVulNum;
    }

    public int getLocalVulNum() {
        return localVulNum;
    }

    public void setLocalVulNum(int localVulNum) {
        this.localVulNum = localVulNum;
    }

    public int getExploitVulNum() {
        return exploitVulNum;
    }

    public void setExploitVulNum(int exploitVulNum) {
        this.exploitVulNum = exploitVulNum;
    }

    public int getOpenVasVulNum() {
        return openVasVulNum;
    }

    public void setOpenVasVulNum(int openVasVulNum) {
        this.openVasVulNum = openVasVulNum;
    }

    public int getVulIpNum() {
        return vulIpNum;
    }

    public void setVulIpNum(int vulIpNum) {
        this.vulIpNum = vulIpNum;
    }

    public int getInControlDeviceVulNum() {
        return inControlDeviceVulNum;
    }

    public void setInControlDeviceVulNum(int inControlDeviceVulNum) {
        this.inControlDeviceVulNum = inControlDeviceVulNum;
    }

    public ArrayList<HashMap<String, String>> getServiceList() {
        return serviceList;
    }

    public void setServiceList(ArrayList<HashMap<String, String>> serviceList) {
        this.serviceList = serviceList;
    }

    public ArrayList<HashMap<String, String>> getVulTypeList() {
        return vulTypeList;
    }

    public void setVulTypeList(ArrayList<HashMap<String, String>> vulTypeList) {
        this.vulTypeList = vulTypeList;
    }
}
