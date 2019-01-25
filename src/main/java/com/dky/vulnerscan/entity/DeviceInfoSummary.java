package com.dky.vulnerscan.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;


//ipInfo中设备识别结果的综合结果
public class DeviceInfoSummary {
    @JsonProperty("device_info_list")
    private ArrayList<DeviceType> deviceInfoList;
    @JsonProperty("device_type_2")
    private String secondDeviceType;//设备类型(二级设备)
    private String model;//设备型号
    @JsonProperty("firmware")
    private String firmWare;//嵌入式设备的固件版本

    public ArrayList<DeviceType> getDeviceInfoList() {
        return deviceInfoList;
    }

    public void setDeviceInfoList(ArrayList<DeviceType> deviceInfoList) {
        this.deviceInfoList = deviceInfoList;
    }

    public String getSecondDeviceType() {
        return secondDeviceType;
    }

    public void setSecondDeviceType(String secondDeviceType) {
        this.secondDeviceType = secondDeviceType;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getFirmWare() {
        return firmWare;
    }

    public void setFirmWare(String firmWare) {
        this.firmWare = firmWare;
    }
}
