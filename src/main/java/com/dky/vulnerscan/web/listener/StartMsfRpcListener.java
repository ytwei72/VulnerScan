package com.dky.vulnerscan.web.listener;


import com.dky.vulnerscan.module.TaskSchedueModule;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.*;
import java.util.Properties;

public class StartMsfRpcListener implements ServletContextListener {
    private static BufferedWriter bw = null;
    private static BufferedReader br = null;
    private static BufferedReader be = null;

    public synchronized static BufferedWriter getBw() {
        return bw;
    }

    public synchronized static void setBw(BufferedWriter bw) {
        StartMsfRpcListener.bw = bw;
    }

    public synchronized static BufferedReader getBr() {
        return br;
    }

    public synchronized static void setBr(BufferedReader br) {
        StartMsfRpcListener.br = br;
    }

    public static BufferedReader getBe() {
        return be;
    }

    public static void setBe(BufferedReader be) {
        StartMsfRpcListener.be = be;
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        InputStream inputStream = TaskSchedueModule.class.getClassLoader().getResourceAsStream("subcontroller.properties");
        Properties p = new Properties();
        try {
            p.load(inputStream);
            String shell = p.getProperty("exploit");
            Runtime rt = Runtime.getRuntime();
            //每次启动之前先把上次的杀死
            rt.exec(new String[]{"/bin/sh", "-c", "kill -9 `ps aux | grep \"MsfRpcClient.py\" | grep -v grep | awk \'{print $2}\'`"});
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Process pr = rt.exec(new String[]{"python", shell});
            br = new BufferedReader(new InputStreamReader(pr.getInputStream(), "UTF-8"));
            be = new BufferedReader(new InputStreamReader(pr.getErrorStream(), "UTF-8"));
            String result = null;
            //等到msfrpc启动之后才能写入
            while ((result = br.readLine()) != null) {
                if (result.contains("starting msfrpc success!")) {
                    bw = new BufferedWriter(new OutputStreamWriter(pr.getOutputStream(), "UTF-8"));
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
