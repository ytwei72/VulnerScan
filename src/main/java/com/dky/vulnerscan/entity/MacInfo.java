package com.dky.vulnerscan.entity;

//ipInfo中mac信息
public class MacInfo {
    private String mac;
    private String vendor;
    private String brand;
    private int similarity;

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
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
