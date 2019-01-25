package com.dky.vulnerscan.entityview;

import java.sql.Timestamp;

/**
 * Created by bo on 2017/4/7.
 */
public class Banner {
    private int id;
    private String protocol; //探测协议
    private int port; //探测端口
    private String encoding; //他侧包编码格式
    private String type; //探测信息是基于tcp还是udp（目前只支持tcp）
    private String fileName;
    private Timestamp addTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Timestamp getAddTime() {
        return addTime;
    }

    public void setAddTime(Timestamp addTime) {
        this.addTime = addTime;
    }
}
