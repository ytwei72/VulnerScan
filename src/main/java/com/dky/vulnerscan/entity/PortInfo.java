package com.dky.vulnerscan.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;


public class PortInfo {
    private int port;//端口
    private String type;//监听端口的传输层类型
    private Service service;//服务探测的结果
    @JsonProperty("device_info")
    private DeviceInfo deviceInfo;//采用单端口关键字匹配算法的设备信息识别结果
    @JsonProperty("vul_list")
    private ArrayList<VulInfo> vulList;//漏洞列表

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public ArrayList<VulInfo> getVulList() {
        return vulList;
    }

    public void setVulList(ArrayList<VulInfo> vulList) {
        this.vulList = vulList;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
}
