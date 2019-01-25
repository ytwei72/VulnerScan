package com.dky.vulnerscan.entity;

import javax.persistence.*;

public class User {
    private int userId;//用户编号
    private String userName;// 用户名
    private String passwd;// 密码
    private String userType;// 用户类型
    private String realName;// 用户真实姓名
    @Transient
    private String newPasswd;// 用户新密码
    @Transient
    private String md5RandomKey;// md5加密的随机数
    private int leftTryNum;// 剩余登录次数
    private long addTime; //检查员添加时间
    private long lastLoginTime;// 最后一次登录时间
    @Transient
    private int loginState;// 登录状态
    private String emailName;
    private String emailPasswd;
    private String emailSubject;
    private String emailContent;
    private int enableManageVulLib;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getNewPasswd() {
        return newPasswd;
    }

    public void setNewPasswd(String newPasswd) {
        this.newPasswd = newPasswd;
    }

    public String getMd5RandomKey() {
        return md5RandomKey;
    }

    public void setMd5RandomKey(String md5RandomKey) {
        this.md5RandomKey = md5RandomKey;
    }

    public int getLeftTryNum() {
        return leftTryNum;
    }

    public void setLeftTryNum(int leftTryNum) {
        this.leftTryNum = leftTryNum;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public int getLoginState() {
        return loginState;
    }

    public void setLoginState(int loginState) {
        this.loginState = loginState;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public String getEmailName() {
        return emailName;
    }

    public void setEmailName(String emailName) {
        this.emailName = emailName;
    }

    public String getEmailPasswd() {
        return emailPasswd;
    }

    public void setEmailPasswd(String emailPasswd) {
        this.emailPasswd = emailPasswd;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    public String getEmailContent() {
        return emailContent;
    }

    public void setEmailContent(String emailContent) {
        this.emailContent = emailContent;
    }

    public int getEnableManageVulLib() {
        return enableManageVulLib;
    }

    public void setEnableManageVulLib(int enableManageVulLib) {
        this.enableManageVulLib = enableManageVulLib;
    }
}
