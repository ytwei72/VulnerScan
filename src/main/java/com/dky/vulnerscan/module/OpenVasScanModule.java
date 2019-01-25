package com.dky.vulnerscan.module;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dky.vulnerscan.service.TaskResultService;
import com.dky.vulnerscan.util.Constant;
import com.dky.vulnerscan.util.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Component("OpenVasScanModule")
public class OpenVasScanModule {
    @Autowired
    private TaskResultService taskResultService;
    private boolean isRunningOpenVasScan = false;
    private OpenVasScanThread openVasScanThread = null;

    public boolean isRunningOpenVasScan() {
        return isRunningOpenVasScan;
    }

    public void setRunningOpenVasScan(boolean runningOpenVasScan) {
        isRunningOpenVasScan = runningOpenVasScan;
    }

    public synchronized void setOpenVasScanThread(OpenVasScanThread openVasScanThread) {
        this.openVasScanThread = openVasScanThread;
    }


    //openVas进行漏洞扫描
    public int openVasDeepScan(int projectID, int taskID, String ip) {
        String ipJson = taskResultService.getIpInfo(projectID, taskID, ip);
        JSONObject jsonObject = JSONObject.parseObject(ipJson);
        //设置品牌为空的不能验证
        if(jsonObject.getJSONObject("device_info_summary").getJSONArray("device_info_list").getJSONObject(0).getString("brand").equals("")){
            return Constant.SUCCESS;
        }
        //正在进行漏洞扫描
        if (isRunningOpenVasScan) {
            return Constant.FAIL;
        }
        try {
            isRunningOpenVasScan = true;
            String[] cmd = new String[3]; //调用openvas进行深度扫描的命令
            InputStream inputStream = OpenVasScanModule.class.getClassLoader().getResourceAsStream("subcontroller.properties");
            Properties p = new Properties();
            p.load(inputStream);
            cmd[0] = "python";
            cmd[1] = p.getProperty("openvas");
            cmd[2] = "@input{" + "\"IP_info\":" + ipJson + "}";
            LogUtil.deepScanLog("openvas cmd:" + projectID + ":" + taskID + ":" + ip + ":" + cmd[2]);
            openVasScanThread = new OpenVasScanThread(projectID, taskID, ip, cmd);
            openVasScanThread.start();
        } catch (IOException e) {
            e.printStackTrace();
            isRunningOpenVasScan = false;
            return Constant.FAIL;
        }
        return Constant.SUCCESS;
    }

    //处理openVas深度扫描的结果
    @Transactional(isolation = Isolation.SERIALIZABLE )
    public void handleOpenVasResult(int projectID, int taskID, String ip, String result) {
        JSONObject jsonIpInfo = JSONObject.parseObject(result.substring(7));//取出ipInfo字段
        if (!jsonIpInfo.containsKey("progress")) {
            JSONArray openVasVulArr = jsonIpInfo.getJSONObject("IP_info").getJSONArray("openvas_vul_list");
            if (openVasVulArr != null) {
                removeOpenVasNull(openVasVulArr);
            }
            //入库之前从数据库中查出最新的数据，保持数据的同步
            String newIpInfo = taskResultService.getIpInfo(projectID, taskID, ip);
            JSONObject jsonNewIpInfo = JSONObject.parseObject(newIpInfo);
            jsonNewIpInfo.put("openvas_vul_list", openVasVulArr);
            taskResultService.saveDeepScanResult(taskID, projectID, ip, jsonNewIpInfo.toString());
        }
    }

    //去除openVas结果中的null，改为""字符串,添加私有字段
    private void removeOpenVasNull(JSONArray openVasVulArr) {
        for (int i = 0; i < openVasVulArr.size(); i++) {
            JSONObject openVasVul = openVasVulArr.getJSONObject(i);
            for (String key : openVasVul.keySet()) {
                if (openVasVul.get(key).equals(null)) {
                    openVasVul.put(key, "");
                }
            }
            openVasVul.put("priv_verify_state", "未验证");
            openVasVul.put("priv_vul_source", "openVAS");
        }
    }

}
