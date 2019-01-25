package com.dky.vulnerscan.entityview;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskResultCount {
    private HashMap<String, Integer> deviceTypeMap;
    private HashMap<String, Integer> vulLevelMap;
    private HashMap<String, Integer> vulDeviceMap;
    private ArrayList<Param> brandList;
    private ArrayList<Param> serviceList;
    private ArrayList<Param> vulStyleList;
    private ArrayList<Param> vulDangerList;
    private ArrayList<Param> osTypeList;


    public HashMap<String, Integer> getDeviceTypeMap() {
        return deviceTypeMap;
    }

    public void setDeviceTypeMap(HashMap<String, Integer> deviceTypeMap) {
        this.deviceTypeMap = deviceTypeMap;
    }

    public ArrayList<Param> getOsTypeList() {
        return osTypeList;
    }

    public void setOsTypeList(ArrayList<Param> osTypeList) {
        this.osTypeList = osTypeList;
    }

    public HashMap<String, Integer> getVulLevelMap() {
        return vulLevelMap;
    }

    public void setVulLevelMap(HashMap<String, Integer> vulLevelMap) {
        this.vulLevelMap = vulLevelMap;
    }

    public HashMap<String, Integer> getVulDeviceMap() {
        return vulDeviceMap;
    }

    public void setVulDeviceMap(HashMap<String, Integer> vulDeviceMap) {
        this.vulDeviceMap = vulDeviceMap;
    }

    public ArrayList<Param> getBrandList() {
        return brandList;
    }

    public void setBrandList(ArrayList<Param> brandList) {
        this.brandList = brandList;
    }

    public ArrayList<Param> getServiceList() {
        return serviceList;
    }

    public void setServiceList(ArrayList<Param> serviceList) {
        this.serviceList = serviceList;
    }

    public ArrayList<Param> getVulStyleList() {
        return vulStyleList;
    }

    public void setVulStyleList(ArrayList<Param> vulStyleList) {
        this.vulStyleList = vulStyleList;
    }

    public ArrayList<Param> getVulDangerList() {
        return vulDangerList;
    }

    public void setVulDangerList(ArrayList<Param> vulDangerList) {
        this.vulDangerList = vulDangerList;
    }
}
