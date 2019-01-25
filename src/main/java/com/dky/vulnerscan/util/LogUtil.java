package com.dky.vulnerscan.util;

//import org.apache.log4j.Logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {
	private static final Logger taskCmd = LoggerFactory.getLogger("runTaskCmdFile");
	private static final Logger taskResult = LoggerFactory.getLogger("taskResultFile");
	private static final Logger scError = LoggerFactory.getLogger("scErrorFile");
	private static final Logger deepScan = LoggerFactory.getLogger("deepScanFile");
	
	//保存每次任务调用控制子软件的命令参数
	public static void taskCmdLog(String cmd){
		taskCmd.debug(cmd);
	}
	
	//保存每次任务执行的结果
	public static void taskResultLog(String result){
		taskResult.debug(result);
	}
	
	//控制子软件错误
	public static void scErrorLog(String error){
		scError.debug(error);
	}

	//深度扫描
	public static void deepScanLog(String scan){
		deepScan.debug(scan);
	}
}
