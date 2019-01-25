package com.dky.vulnerscan.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;



public class PropertyUtil {
	private String driverClassName;
	private String url;
	private String userName;
	private String pwd;
	private  String[] DBinfos;
	private  Properties props;
	private  int maxIdleTime;
	public  PropertyUtil(){
		loadProps();
	}

    private void loadProps(){
        props = new Properties();
        InputStream in = null;
        try {
        	URL url=PropertyUtil.class.getClassLoader().getResource("jdbc.properties");
        	if(url == null){
        		loadDecry();
        		return;
        	}
			in = PropertyUtil.class.getClassLoader().getResourceAsStream("jdbc.properties");
	        props.load(in);
	        setDriverClassName(props.getProperty("jdbc.driverClassName"));
	        setUrl(props.getProperty("jdbc.url"));
	        setUserName(props.getProperty("jdbc.username"));
	        setPwd(props.getProperty("jdbc.password"));
	        setMaxIdleTime(Integer.parseInt(props.getProperty("jdbc.maxIdleTime")));
        } catch (FileNotFoundException e) {
        	e.printStackTrace();
        } catch (IOException e) {
           e.printStackTrace();
        } finally {
            try {
                if(null != in) {
                    in.close();
                }
            } catch (IOException e) {
               e.printStackTrace();
            }
        }
    }

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public int getMaxIdleTime() {
		return maxIdleTime;
	}

	public void setMaxIdleTime(int maxIdleTime) {
		this.maxIdleTime = maxIdleTime;
	}

	public void loadDecry(){
		DBinfos=new LoadDecry().getDBinfos();
		System.out.println("解密数据库"+new LoadDecry().isDecry());
		if(DBinfos!=null && DBinfos.length>0){
    		String driverClassName="com.mysql.jdbc.Driver";
    		int maxIdleTime=1800;
        	String url="jdbc:mysql://"+DBinfos[0]+":3306"+"/"+DBinfos[3]+"?useUnicode=true&amp;characterEncoding=UTF-8&autoReconnect=true";
        	String user=DBinfos[1];
        	String pwd=DBinfos[2];
        	setDriverClassName(driverClassName);
        	setUrl(url);
        	setUserName(user);
        	setPwd(pwd);
        	setMaxIdleTime(maxIdleTime);
    	}
	}
}
