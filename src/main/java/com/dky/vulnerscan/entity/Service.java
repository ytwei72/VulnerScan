package com.dky.vulnerscan.entity;

public class Service {
    private String type;//协议或者软件类型
    private String product;//软件或者协议实现名称
    private String version;//版本号

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
