package com.dky.vulnerscan.entity;

/**
 * Created by bo on 2017/4/9.
 */
public class ProbeConfig {
    private String section;
    private String type;
    private int port;
    private String req;
    private String encoding;
    private String zgrabDefault;

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getReq() {
        return req;
    }

    public void setReq(String req) {
        this.req = req;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getZgrabDefault() {
        return zgrabDefault;
    }

    public void setZgrabDefault(String zgrabDefault) {
        this.zgrabDefault = zgrabDefault;
    }
}
