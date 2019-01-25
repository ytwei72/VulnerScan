package com.dky.vulnerscan.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;


//ipInfo中Onvif识别结果
@JsonIgnoreProperties(value = {"priv_debug_info"})
public class OnvifInfo {
    private int port;
    @JsonProperty("priv_debug_info")
    private DebugInfo debugInfo;
    @JsonProperty("image_path")
    private String imagePath;
    @JsonProperty("device_info")
    private DeviceInfo deviceInfo;
    @JsonProperty("vul_list")
    private ArrayList<VulInfo> vulList;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public DebugInfo getDebugInfo() {
        return debugInfo;
    }

    public void setDebugInfo(DebugInfo debugInfo) {
        this.debugInfo = debugInfo;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
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
