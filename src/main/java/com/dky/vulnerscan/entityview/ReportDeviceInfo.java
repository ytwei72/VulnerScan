package com.dky.vulnerscan.entityview;

import java.util.ArrayList;

/**
 * FileName: ReportDeviceInfo.java
 *  用于构造报表中的设备详情表格的数据信息
 *
 *  @author liushaoshuai on 2016/12/15.
 */
public class ReportDeviceInfo {
    private String ip;
    private String brand;
    private String os;
    private ArrayList<ReportVulInfo> reportVulInfos;//全部漏洞库列表
    private ArrayList<ReportVulInfo> localVulInfos;//本地漏洞库列表
    private ArrayList<ReportVulInfo> exploitVulInfos;//Exploit漏洞库列表
    private ArrayList<ReportVulInfo> openvasVulInfos;//Openvas漏洞库列表

    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public ArrayList<ReportVulInfo> getReportVulInfos() {
        return reportVulInfos;
    }

    public void setReportVulInfos(ArrayList<ReportVulInfo> reportVulInfos) {
        this.reportVulInfos = reportVulInfos;
    }

    public ArrayList<ReportVulInfo> getLocalVulInfos() {
        return localVulInfos;
    }

    public void setLocalVulInfos(ArrayList<ReportVulInfo> localVulInfos) {
        this.localVulInfos = localVulInfos;
    }

    public ArrayList<ReportVulInfo> getExploitVulInfos() {
        return exploitVulInfos;
    }

    public void setExploitVulInfos(ArrayList<ReportVulInfo> exploitVulInfos) {
        this.exploitVulInfos = exploitVulInfos;
    }

    public ArrayList<ReportVulInfo> getOpenvasVulInfos() {
        return openvasVulInfos;
    }

    public void setOpenvasVulInfos(ArrayList<ReportVulInfo> openvasVulInfos) {
        this.openvasVulInfos = openvasVulInfos;
    }
}
