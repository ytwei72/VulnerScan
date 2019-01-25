package com.dky.vulnerscan.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VulInfo {
    private String id;//漏洞编号
    private int checked;//漏洞是否验证，0：没有，1：验证了
    private String msg;
    @JsonProperty("priv_fixed")
    private int fixed;//漏洞是否修复
    @JsonProperty("priv_vul_tech_type")
    private String vulType;
    @JsonProperty("priv_vul_title")
    private String vulTitle;
    @JsonProperty("priv_vul_level")
    private String vulLevel;
    @JsonProperty("priv_vul_affect")
    private String vulAffect;
    @JsonProperty("priv_vul_solution")
    private String vulSolution;
    @JsonProperty("priv_verify_state")
    private String verifyState;
    @JsonProperty("priv_vul_source")
    private String vulSource;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getChecked() {
        return checked;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getFixed() {
        return fixed;
    }

    public void setFixed(int fixed) {
        this.fixed = fixed;
    }

    public String getVulType() {
        return vulType;
    }

    public void setVulType(String vulType) {
        this.vulType = vulType;
    }

    public String getVulTitle() {
        return vulTitle;
    }

    public void setVulTitle(String vulTitle) {
        this.vulTitle = vulTitle;
    }

    public String getVulLevel() {
        return vulLevel;
    }

    public void setVulLevel(String vulLevel) {
        this.vulLevel = vulLevel;
    }

    public String getVulAffect() {
        return vulAffect;
    }

    public void setVulAffect(String vulAffect) {
        this.vulAffect = vulAffect;
    }

    public String getVulSolution() {
        return vulSolution;
    }

    public void setVulSolution(String vulSolution) {
        this.vulSolution = vulSolution;
    }

    public String getVerifyState() {
        return verifyState;
    }

    public void setVerifyState(String verifyState) {
        this.verifyState = verifyState;
    }

    public String getVulSource() {
        return vulSource;
    }

    public void setVulSource(String vulSource) {
        this.vulSource = vulSource;
    }
}
