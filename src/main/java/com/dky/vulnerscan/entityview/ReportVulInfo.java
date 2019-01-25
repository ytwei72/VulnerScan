package com.dky.vulnerscan.entityview;

import com.dky.vulnerscan.entity.VulInfo;
import com.dky.vulnerscan.entity.VulInfo;

import java.util.ArrayList;

/**
 * FileName ReportVulInfo.java
 * 用于构造报表中的每个端口/服务下的漏洞列表信息
 * <p>
 * Created by cyberpecker on 2016/12/15.
 */
public class ReportVulInfo {
    private String service;//网络服务
    private String type;  //协议
    private String port; //端口
    private String stp;
    private ArrayList<VulInfo> vulInfos; //端口下的漏洞列表

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public ArrayList<VulInfo> getVulInfos() {
        return vulInfos;
    }

    public void setVulInfos(ArrayList<VulInfo> vulInfos) {
        this.vulInfos = vulInfos;
    }

    public String getStp() {
        String temp = "", ser = "", ty = "", po = "";
        ser = this.getService();
        ty = this.getType();
        po = this.getPort();
        if (ser.length() > 0) {
            temp += ser;
        }
        if (ser.length() > 0 && ty.length() > 0) {
            temp += "，" + ty;
        }
        if (ser.length() > 0 && ty.length() > 0 && po.length() > 0) {
            temp += "，" + po;
        }
        setStp(temp);
        return stp;
    }

    public void setStp(String stp) {
        this.stp = stp;
    }
}
