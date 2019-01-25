package com.dky.vulnerscan.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


/**
 * @filename: LoadDecry.java
 * @date: 2016年11月21日
 * @author: achelics
 * @since: jdk1.8
 * @version: 1.0
 * @description:
 **/

public class LoadDecry {

	/** 需要执行的解密程序所在的位置 */
	private String loadDecryCmd = "/root/electric/cyber_config/cyberlib_decry";
	private String character = "UTF-8";// 设置读取的字符编码
	private Process process = null;
	private BufferedWriter bw = null;
	private BufferedReader br = null;
	private BufferedReader be = null;
	private boolean decryFlag = true;
	private String[] DBinfos;
	public LoadDecry() {
		startDecryProcess();
	}

	/**
	 * @description: 执行解密文件操作
	 * */
	private void startDecryProcess() {

		Runtime rt = Runtime.getRuntime();
		String[] cmdA = { "/bin/sh", "-c", loadDecryCmd };
		try {
			process = rt.exec(cmdA);
			
			br = new BufferedReader(new InputStreamReader(process.getInputStream(), character));
			be = new BufferedReader(new InputStreamReader(process.getErrorStream(), character));
			bw = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(),character));
			String line = null;
			String[] tempInfos;
		try {
			while ((line = br.readLine()) != null) {
					if(line.indexOf("False") == -1) {
						tempInfos=line.split(";");
						setDBinfos(tempInfos);
						decryFlag = true;
					}else{
						decryFlag = false;
			}
		}
		}catch (IOException e) {
			// 300
		//	OutputHandler.outputError(OutputHandler.decryLoadError,
		//			e.toString(), "SC");
			System.exit(0);
		}

		} catch (IOException e) {
			// 300
		//	OutputHandler.outputError(OutputHandler.decryLoadError,
		//		e.toString(), "SC");
			System.exit(0);
		}
	}

	/**
	 * @description: 解析标准输出
	 * @return bool
	 * */

	public boolean isDecry() {
		
		return decryFlag;
	}

	public String[] getDBinfos() {
		return DBinfos;
	}

	public void setDBinfos(String[] dBinfos) {
		String[] infos=new String[dBinfos.length-2];
		for(int i=0;i<dBinfos.length-2;i++){
			String[] temp=dBinfos[i].split("=");
			infos[i]=temp[1];
		}
		DBinfos = infos; //url,user,pawd,database
	}
	
}
