package com.dky.vulnerscan.entity;

public class ReceiverEmail {
    private int emailID;
    private String userName;
    private String receiverName;
    private String receiverEmailAddress;

    public int getEmailID() {
        return emailID;
    }

    public void setEmailID(int emailID) {
        this.emailID = emailID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverEmailAddress() {
        return receiverEmailAddress;
    }

    public void setReceiverEmailAddress(String receiverEmailName) {this.receiverEmailAddress = receiverEmailName;
    }
}
