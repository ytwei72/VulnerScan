package com.dky.vulnerscan.entity;

public class Network {
    private String ethName;

    private String ip;

    private String netmask;

    private String gateway;

    private String dns1;

    private String dns2;

    private Integer dhcpFlag;

    private Integer bandwidth;

    public Network(String ethName, String ip, String netmask, String gateway, String dns1, String dns2, Integer dhcpFlag, Integer bandwidth) {
        this.ethName = ethName;
        this.ip = ip;
        this.netmask = netmask;
        this.gateway = gateway;
        this.dns1 = dns1;
        this.dns2 = dns2;
        this.dhcpFlag = dhcpFlag;
        this.bandwidth = bandwidth;
    }

    public Network() {
        super();
    }

    public String getEthName() {
        return ethName;
    }

    public void setEthName(String ethName) {
        this.ethName = ethName == null ? null : ethName.trim();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip == null ? null : ip.trim();
    }

    public String getNetmask() {
        return netmask;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask == null ? null : netmask.trim();
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway == null ? null : gateway.trim();
    }

    public String getDns1() {
        return dns1;
    }

    public void setDns1(String dns1) {
        this.dns1 = dns1 == null ? null : dns1.trim();
    }

    public String getDns2() {
        return dns2;
    }

    public void setDns2(String dns2) {
        this.dns2 = dns2 == null ? null : dns2.trim();
    }

    public Integer getDhcpFlag() {
        return dhcpFlag;
    }

    public void setDhcpFlag(Integer dhcpFlag) {
        this.dhcpFlag = dhcpFlag;
    }

    public Integer getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Integer bandwidth) {
        this.bandwidth = bandwidth;
    }
}