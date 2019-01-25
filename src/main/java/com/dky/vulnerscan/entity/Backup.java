package com.dky.vulnerscan.entity;

public class Backup {

	private Integer id;
	private String time;
	private String remark;

    public Backup () {
    }

	public Backup (Integer id, String time, String remark) {
	    this.id=id;
	    this.time=time;
	    this.remark=remark;
    }
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
}
