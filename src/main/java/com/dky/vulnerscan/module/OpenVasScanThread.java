package com.dky.vulnerscan.module;


import com.dky.vulnerscan.util.LogUtil;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import java.io.*;

public class OpenVasScanThread extends Thread {
    private int projectID = 0;
    private int taskID = 0;
    private String ip = "";
    private BufferedReader br = null;
    private WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
    private OpenVasScanModule openVasScanModule = (OpenVasScanModule) context.getBean("OpenVasScanModule");

    public OpenVasScanThread(int projectID, int taskID, String ip, String[] cmd) throws IOException {
        this.projectID = projectID;
        this.taskID = taskID;
        this.ip = ip;

        Runtime rt = Runtime.getRuntime();
        Process pr = rt.exec(cmd);
        br = new BufferedReader(new InputStreamReader(pr.getInputStream(), "UTF-8"));
    }

    @Override
    public void run() {
        String result = null;
        try {
            while ((result = br.readLine()) != null) {
                LogUtil.deepScanLog("openVas:" + projectID + ":" + taskID + ":" + ip + ":" + result);
                if (result.indexOf("@output") == 0) {
                    openVasScanModule.handleOpenVasResult(projectID, taskID, ip, result);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            openVasScanModule.setRunningOpenVasScan(false);
            openVasScanModule.setOpenVasScanThread(null);
        }
    }
}
