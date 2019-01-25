package com.dky.vulnerscan.entity;

public class Project {
    private int projectID;
    private String projectName;//项目名称
    private String checker;
    private String describe;//项目描述
    private String target;//主机发现要扫描的ip
    private String hdAdditionPort;//主机发现需要指定的额外端口
    private int intensity;//主机发现扫描强度等级
    private String protocol;//主机发现探测包使用的协议
    private String blackList;//黑名单
    private int enableTopoProbe;//开启拓扑发现
    private String scanType;//服务探测端口使用的扫描类型
    private String sdAdditionPortTcp;
    private String sdAdditionPortUdp;
    private int topPorts;//端口扫描使用的端口数目
    private String excludePorts;//指定无需扫描的具体端口
    private int enableVersionDetec;//开启服务版本探测
    private int versionIntensity;//服务版本他侧强度
    private int enableOsDetec;//开启操作系统信息探测
    private String probeModule;//弱口令探测要探测的目标协议
    private int enableReboot;//漏洞是否重启标识
    private int enableChange;//漏洞是否修改
    private int projectFlag;//项目标识(周期/一次)
    private int space;//之间间隔
    private long time;
    private long nextTime;//下次执行的时间
    private int times;   //执行次数
    private long createTime; //项目创建日期
    private String state;

    public int getProjectID() {
        return projectID;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getChecker() {
        return checker;
    }

    public void setChecker(String checker) {
        this.checker = checker;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getHdAdditionPort() {
        return hdAdditionPort;
    }

    public void setHdAdditionPort(String hdAdditionPort) {
        this.hdAdditionPort = hdAdditionPort;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getBlackList() {
        return blackList;
    }

    public void setBlackList(String blackList) {
        this.blackList = blackList;
    }

    public int getEnableTopoProbe() {
        return enableTopoProbe;
    }

    public void setEnableTopoProbe(int enableTopoProbe) {
        this.enableTopoProbe = enableTopoProbe;
    }

    public String getScanType() {
        return scanType;
    }

    public void setScanType(String scanType) {
        this.scanType = scanType;
    }

    public String getSdAdditionPortTcp() {
        return sdAdditionPortTcp;
    }

    public void setSdAdditionPortTcp(String sdAdditionPortTcp) {
        this.sdAdditionPortTcp = sdAdditionPortTcp;
    }

    public String getSdAdditionPortUdp() {
        return sdAdditionPortUdp;
    }

    public void setSdAdditionPortUdp(String sdAdditionPortUdp) {
        this.sdAdditionPortUdp = sdAdditionPortUdp;
    }

    public int getTopPorts() {
        return topPorts;
    }

    public void setTopPorts(int topPorts) {
        this.topPorts = topPorts;
    }

    public String getExcludePorts() {
        return excludePorts;
    }

    public void setExcludePorts(String excludePorts) {
        this.excludePorts = excludePorts;
    }

    public int getEnableVersionDetec() {
        return enableVersionDetec;
    }

    public void setEnableVersionDetec(int enableVersionDetec) {
        this.enableVersionDetec = enableVersionDetec;
    }

    public int getVersionIntensity() {
        return versionIntensity;
    }

    public void setVersionIntensity(int versionIntensity) {
        this.versionIntensity = versionIntensity;
    }

    public int getEnableOsDetec() {
        return enableOsDetec;
    }

    public void setEnableOsDetec(int enableOsDetec) {
        this.enableOsDetec = enableOsDetec;
    }

    public String getProbeModule() {
        return probeModule;
    }

    public void setProbeModule(String probeModule) {
        this.probeModule = probeModule;
    }

    public int getEnableReboot() {
        return enableReboot;
    }

    public void setEnableReboot(int enableReboot) {
        this.enableReboot = enableReboot;
    }

    public int getEnableChange() {
        return enableChange;
    }

    public void setEnableChange(int enableChange) {
        this.enableChange = enableChange;
    }

    public int getProjectFlag() {
        return projectFlag;
    }

    public void setProjectFlag(int projectFlag) {
        this.projectFlag = projectFlag;
    }

    public int getSpace() {
        return space;
    }

    public void setSpace(int space) {
        this.space = space;
    }

    public long getNextTime() {
        return nextTime;
    }

    public void setNextTime(long nextTime) {
        this.nextTime = nextTime;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
