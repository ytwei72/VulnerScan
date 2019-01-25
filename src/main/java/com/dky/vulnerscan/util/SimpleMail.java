package com.dky.vulnerscan.util;

public class SimpleMail {
	
	private String subject; //邮件标题
	private String content; //邮件内容
	private String taskReportPath; //报表URL
	private String from; //发件人
	private String to; //多个联系人间用","间隔
	
	private String username;
	private String password;
	private String protocol;
	private String host;
	
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTaskReportPath() {
		return taskReportPath;
	}
	public void setTaskReportPath(String taskReportPath) {
		this.taskReportPath = taskReportPath;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
}
