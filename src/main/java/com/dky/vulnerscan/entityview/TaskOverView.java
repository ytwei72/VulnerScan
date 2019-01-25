package com.dky.vulnerscan.entityview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TaskOverView {
    private String state;
    private long startTime;
    private int takingTime;
    private int counts;
    private int deviceNum;
    private int videoNum;
    private int vulNum;
    ArrayList<Param> progress;
    private int totalProgress;
    private ArrayList<Map<String, String>> errorList;
    private String reportFormPath;
    private Double realBandWidth;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getTakingTime() {
        return takingTime;
    }

    public void setTakingTime(int takingTime) {
        this.takingTime = takingTime;
    }

    public int getCounts() {
        return counts;
    }

    public void setCounts(int counts) {
        this.counts = counts;
    }

    public ArrayList<Param> getProgress() {
        return progress;
    }

    public void setProgress(ArrayList<Param> progress) {
        this.progress = progress;
    }

    public int getTotalProgress() {
        return totalProgress;
    }

    public void setTotalProgress(int totalProgress) {
        this.totalProgress = totalProgress;
    }

    public ArrayList<Map<String, String>> getErrorList() {
        return errorList;
    }

    public void setErrorList(ArrayList<Map<String, String>> errorList) {
        this.errorList = errorList;
    }

    public String getReportFormPath() {
        return reportFormPath;
    }

    public void setReportFormPath(String reportFormPath) {
        this.reportFormPath = reportFormPath;
    }

    public int getDeviceNum() {
        return deviceNum;
    }

    public void setDeviceNum(int deviceNum) {
        this.deviceNum = deviceNum;
    }

    public int getVideoNum() {
        return videoNum;
    }

    public void setVideoNum(int videoNum) {
        this.videoNum = videoNum;
    }

    public int getVulNum() {
        return vulNum;
    }

    public void setVulNum(int vulNum) {
        this.vulNum = vulNum;
    }

    public Double getRealBandWidth() {
        return realBandWidth;
    }

    public void setRealBandWidth(Double realBandWidth) {
        this.realBandWidth = realBandWidth;
    }
}
