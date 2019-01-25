package com.dky.vulnerscan.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(value = {"FP_data", "FP_data_onvif"})
public class DeviceInfo {
    @JsonProperty("device_type_1")
    private String firstDeviceType;//设备类型(一级设备)
    @JsonProperty("device_type_2")
    private String secondDeviceType;//设备类型(二级设备)
    private int similarity;
    private String brand;//品牌
    private String model;//设备型号
    @JsonProperty("firmware")
    private String firmWare;//嵌入式设备的固件版本
    @JsonProperty("FP_data")
    private FPData fpData;//标语信息
    @JsonProperty("FP_data_onvif")
    private FPDataOnvif fpDataOnvif;

    public String getFirstDeviceType() {
        return firstDeviceType;
    }

    public void setFirstDeviceType(String firstDeviceType) {
        this.firstDeviceType = firstDeviceType;
    }

    public String getSecondDeviceType() {
        return secondDeviceType;
    }

    public void setSecondDeviceType(String secondDeviceType) {
        this.secondDeviceType = secondDeviceType;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
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

    public FPData getFpData() {
        return fpData;
    }

    public void setFpData(FPData fpData) {
        this.fpData = fpData;
    }

    public int getSimilarity() {
        return similarity;
    }

    public void setSimilarity(int similarity) {
        this.similarity = similarity;
    }

    public FPDataOnvif getFpDataOnvif() {
        return fpDataOnvif;
    }

    public void setFpDataOnvif(FPDataOnvif fpDataOnvif) {
        this.fpDataOnvif = fpDataOnvif;
    }
}
