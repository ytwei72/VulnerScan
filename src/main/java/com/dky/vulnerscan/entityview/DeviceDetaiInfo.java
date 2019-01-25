package com.dky.vulnerscan.entityview;

import java.util.ArrayList;
import java.util.Map;

public class DeviceDetaiInfo implements Comparable<DeviceDetaiInfo> {
    private String ip;
    private String style;
    private String brand;
    private ArrayList<Map<String, String>> service;
    private VulCount vulCount;
    private int serviceCount;
    private ArrayList<VulDetail> vulDes;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public ArrayList<Map<String, String>> getService() {
        return service;
    }

    public void setService(ArrayList<Map<String, String>> service) {
        this.service = service;
    }

    public VulCount getVulCount() {
        return vulCount;
    }

    public void setVulCount(VulCount vulCount) {
        this.vulCount = vulCount;
    }

    public int getServiceCount() {
        return serviceCount;
    }

    public void setServiceCount(int serviceCount) {
        this.serviceCount = serviceCount;
    }

    public ArrayList<VulDetail> getVulDes() {
        return vulDes;
    }

    public void setVulDes(ArrayList<VulDetail> vulDes) {
        this.vulDes = vulDes;
    }

    @Override
    public int compareTo(DeviceDetaiInfo o) {
        String[] ip1 = this.getIp().split("\\.");
        String[] ip2 = o.getIp().split("\\.");
        for (int i = 0; i < ip1.length; i++) {
            if (Integer.parseInt(ip1[i]) != Integer.parseInt(ip2[i])) {
                return Integer.parseInt(ip1[i]) - Integer.parseInt(ip2[i]);
            }
        }
        return 0;

    }

}
