package com.dky.vulnerscan.entity;

import com.dky.vulnerscan.entityview.VulCount;


public class Task {
    private int taskID;
    private int projectID;
    private int counts;
    private String checker;
    private int totalProgress;
    private int hdProgress;
    private int sdProgress;
    private int odProgress;
    private int drProgress;
    private int wpProgress;
    private int vsProgress;
    private String state;
    private int iscancel;
    private int compeletedFlag;
    private long startTime;
    private int takingTime;
    private int properties;
    private VulCount vulCount;
    private String taskError;
    private String reportUrl;
    private double usedFlow;

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public int getProjectID() {
        return projectID;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public int getCounts() {
        return counts;
    }

    public void setCounts(int counts) {
        this.counts = counts;
    }

    public String getChecker() {
        return checker;
    }

    public void setChecker(String checker) {
        this.checker = checker;
    }

    public int getTotalProgress() {
        return totalProgress;
    }

    public void setTotalProgress(int totalProgress) {
        this.totalProgress = totalProgress;
    }

    public int getHdProgress() {
        return hdProgress;
    }

    public void setHdProgress(int hdProgress) {
        this.hdProgress = hdProgress;
    }

    public int getSdProgress() {
        return sdProgress;
    }

    public void setSdProgress(int sdProgress) {
        this.sdProgress = sdProgress;
    }

    public int getOdProgress() {
        return odProgress;
    }

    public void setOdProgress(int odProgress) {
        this.odProgress = odProgress;
    }

    public int getDrProgress() {
        return drProgress;
    }

    public void setDrProgress(int drProgress) {
        this.drProgress = drProgress;
    }

    public int getWpProgress() {
        return wpProgress;
    }

    public void setWpProgress(int wpProgress) {
        this.wpProgress = wpProgress;
    }

    public int getVsProgress() {
        return vsProgress;
    }

    public void setVsProgress(int vsProgress) {
        this.vsProgress = vsProgress;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getIscancel() {
        return iscancel;
    }

    public void setIscancel(int iscancel) {
        this.iscancel = iscancel;
    }

    public int getCompeletedFlag() {
        return compeletedFlag;
    }

    public void setCompeletedFlag(int compeletedFlag) {
        this.compeletedFlag = compeletedFlag;
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

    public int getProperties() {
        return properties;
    }

    public void setProperties(int properties) {
        this.properties = properties;
    }

    public VulCount getVulCount() {
        return vulCount;
    }

    public void setVulCount(VulCount vulCount) {
        this.vulCount = vulCount;
    }

    public String getTaskError() {
        return taskError;
    }

    public void setTaskError(String taskError) {
        this.taskError = taskError;
    }

    public String getReportUrl() {
        return reportUrl;
    }

    public void setReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
    }

    public double getUsedFlow() {
        return usedFlow;
    }

    public void setUsedFlow(double usedFlow) {
        this.usedFlow = usedFlow;
    }
}
