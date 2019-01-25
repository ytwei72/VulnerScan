package com.dky.vulnerscan.entityview;

public class VulDetail {
    private String vulID;
    private String service;
    private String port;
    private String name;
    private String grade;
    private String style;
    private String danger;
    private String solution;
    private String checkedState;
    private String hasCheckedScript;
    private String source;

    public String getVulID() {
        return vulID;
    }

    public void setVulID(String vulID) {
        this.vulID = vulID;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getDanger() {
        return danger;
    }

    public void setDanger(String danger) {
        this.danger = danger;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String getCheckedState() {
        return checkedState;
    }

    public void setCheckedState(String checkedState) {
        this.checkedState = checkedState;
    }

    public String getHasCheckedScript() {
        return hasCheckedScript;
    }

    public void setHasCheckedScript(String hasCheckedScript) {
        this.hasCheckedScript = hasCheckedScript;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
