package com.dky.vulnerscan.entity;


import com.fasterxml.jackson.annotation.JsonProperty;

//设备识别结果的综合结果
public class DeviceType {
    @JsonProperty("device_type_1")
    private String firstDeviceType;
    private String brand;
    private int similarity;

    public String getFirstDeviceType() {
        return firstDeviceType;
    }

    public void setFirstDeviceType(String firstDeviceType) {
        this.firstDeviceType = firstDeviceType;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getSimilarity() {
        return similarity;
    }

    public void setSimilarity(int similarity) {
        this.similarity = similarity;
    }
}
