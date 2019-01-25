//package com.dky.vulnerscan.web.listener;
//
//
//import org.springframework.web.util.Log4jConfigListener;
//
//import javax.servlet.ServletContextEvent;
//
//
////设置日志的保存路径
//public class Log4jConfigListenerExt extends Log4jConfigListener {
//
//	@Override
//	public void contextDestroyed(ServletContextEvent event) {
//		super.contextDestroyed(event);
//	}
//
//	@Override
//	public void contextInitialized(ServletContextEvent event) {
//		//设置日志记录的路径，对应于log4j.properties中的$(log4j_path)
//        System.setProperty("log4j_path",event.getServletContext().getRealPath("/log"));
//		super.contextInitialized(event);
//	}
//
//}
