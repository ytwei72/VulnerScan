package com.dky.vulnerscan.entity;


public class TaskResult {
    private int taskID;
    private int projectID;
    private String ipAddr;
    private String ipInfo;

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

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getIpInfo() {
        return ipInfo;
    }

    public void setIpInfo(String ipInfo) {
        this.ipInfo = ipInfo;
    }
}
