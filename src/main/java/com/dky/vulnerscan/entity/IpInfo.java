package com.dky.vulnerscan.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;


//ipInfo信息对应的实体类
public class IpInfo {
    private String ip;//主机ip
    @JsonProperty("mac_info")
    private MacInfo macInfo;//主机的mac地址
    @JsonProperty("nmap_device_type")
    private String nmapDeviceType;//nmap给出的设备信息设备类型(二级类型)
    private String os;//操作系统信息
    @JsonProperty("device_info_summary")
    private DeviceInfoSummary deviceInfoSummary;//关键字匹配识别算法的综合结果
    private OnvifInfo onvif;//onvif识别的结果
    @JsonProperty("port_list")
    private ArrayList<PortInfo> portList;//主机对应的所有端口识别信息
    @JsonProperty("openvas_vul_list")
    private ArrayList<Openvas> openvasVulList;
    private Exploit exploit;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getNmapDeviceType() {
        return nmapDeviceType;
    }

    public void setNmapDeviceType(String nmapDeviceType) {
        this.nmapDeviceType = nmapDeviceType;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public DeviceInfoSummary getDeviceInfoSummary() {
        return deviceInfoSummary;
    }

    public void setDeviceInfoSummary(DeviceInfoSummary deviceInfoSummary) {
        this.deviceInfoSummary = deviceInfoSummary;
    }

    public OnvifInfo getOnvif() {
        return onvif;
    }

    public void setOnvif(OnvifInfo onvif) {
        this.onvif = onvif;
    }

    public ArrayList<PortInfo> getPortList() {
        return portList;
    }

    public void setPortList(ArrayList<PortInfo> portList) {
        this.portList = portList;
    }

    public MacInfo getMacInfo() {
        return macInfo;
    }

    public void setMacInfo(MacInfo macInfo) {
        this.macInfo = macInfo;
    }

    public ArrayList<Openvas> getOpenvasVulList() {
        return openvasVulList;
    }

    public void setOpenvasVulList(ArrayList<Openvas> openvasVulList) {
        this.openvasVulList = openvasVulList;
    }

    public Exploit getExploit() {
        return exploit;
    }

    public void setExploit(Exploit exploit) {
        this.exploit = exploit;
    }
}
