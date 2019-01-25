package com.dky.vulnerscan.entity;


import com.fasterxml.jackson.annotation.JsonProperty;

public class DebugInfo {
    private String path;
    @JsonProperty("brand_3702")
    private String portBrand;
    @JsonProperty("media_path")
    private String mediaPath;
    @JsonProperty("profile_token")
    private String profileToken;
    @JsonProperty("snapshot_path")
    private String snapshotPath;
    @JsonProperty("brand_dev_ser")
    private String brandDevSer;
    @JsonProperty("model_dev_ser")
    private String modelDevSer;
    @JsonProperty("firmware_dev_ser")
    private String firmwareDevSer;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPortBrand() {
        return portBrand;
    }

    public void setPortBrand(String portBrand) {
        this.portBrand = portBrand;
    }

    public String getMediaPath() {
        return mediaPath;
    }

    public void setMediaPath(String mediaPath) {
        this.mediaPath = mediaPath;
    }

    public String getProfileToken() {
        return profileToken;
    }

    public void setProfileToken(String profileToken) {
        this.profileToken = profileToken;
    }

    public String getSnapshotPath() {
        return snapshotPath;
    }

    public void setSnapshotPath(String snapshotPath) {
        this.snapshotPath = snapshotPath;
    }

    public String getBrandDevSer() {
        return brandDevSer;
    }

    public void setBrandDevSer(String brandDevSer) {
        this.brandDevSer = brandDevSer;
    }

    public String getModelDevSer() {
        return modelDevSer;
    }

    public void setModelDevSer(String modelDevSer) {
        this.modelDevSer = modelDevSer;
    }

    public String getFirmwareDevSer() {
        return firmwareDevSer;
    }

    public void setFirmwareDevSer(String firmwareDevSer) {
        this.firmwareDevSer = firmwareDevSer;
    }
}
