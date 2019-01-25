package com.dky.vulnerscan.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(value = "description")
public class Openvas {
    private String nvt;
    private String description;
    @JsonProperty("scan_nvt_version")
    private String scanNvtVersion;
    private String threat;
    private String port;
    private String name;
    @JsonProperty("priv_verify_state")
    private String verifyState;
    @JsonProperty("priv_vul_source")
    private String vulSource;

    public String getNvt() {
        return nvt;
    }

    public void setNvt(String nvt) {
        this.nvt = nvt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getScanNvtVersion() {
        return scanNvtVersion;
    }

    public void setScanNvtVersion(String scanNvtVersion) {
        this.scanNvtVersion = scanNvtVersion;
    }

    public String getThreat() {
        return threat;
    }

    public void setThreat(String threat) {
        this.threat = threat;
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
