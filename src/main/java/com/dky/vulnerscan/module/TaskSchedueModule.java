package com.dky.vulnerscan.module;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.dky.vulnerscan.entityview.ReportResultCount;
import com.dky.vulnerscan.service.*;
import com.dky.vulnerscan.util.ReportUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dky.vulnerscan.entity.Project;
import com.dky.vulnerscan.entity.Task;
import com.dky.vulnerscan.util.Constant;
import com.dky.vulnerscan.util.LogUtil;
import org.springframework.web.context.ContextLoader;

@Component("TaskSchedueModule")
public class TaskSchedueModule {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private NetConfService netConfService;
    @Autowired
    private TaskResultService taskResultService;
    @Autowired
    private ReportService reportService;
    private RunTaskThread runningThread = null; //记录当前正在执行任务的线程
    private String strError = "";   //用"#"区分该任务执行过程中产生的错误信息

    public synchronized RunTaskThread getRunningThread() {
        return runningThread;
    }

    public synchronized void setRunningThread(RunTaskThread runningThread) {
        this.runningThread = runningThread;
    }

    //Spring周期性执行该函数
    public void taskScheduer() {
        pollPeriodicProject();
        tryScanTask();
    }

    // 轮训项目表中的周期型项目，如果有项目到期，则生成一个任务执行
    public void pollPeriodicProject() {
        List<Project> periodicProjList = projectService.getPeriodicProject();// 查询所有的周期性项目
        long currentTime = System.currentTimeMillis();// 系统当前时间
        for (int i = 0; i < periodicProjList.size(); i++) {
            long projectNextTime = periodicProjList.get(i).getNextTime();
            // 判断这次轮训的项目是否到期,有则生成任务
            if (currentTime >= projectNextTime) {
                // 判断此任务队列中是否还有项目上次生成的生成的任务排队
                boolean flag = taskService.hasUnfinTask(periodicProjList.get(i).getProjectID());
                if (!flag) {
                    tryCreateTask(periodicProjList.get(i));
                }
                projectService.updateNextTime(periodicProjList.get(i).getProjectID());
            }
        }
    }

    // 生成指定项目的一个任务
    public int tryCreateTask(Project project) {
        if(project == null){
            return Constant.FAIL;
        }
        boolean flag = taskService.hasUnfinTask(project.getProjectID()); //判断这个项目是否有对应的未完成的任务
        if (flag) {// 如果任务表中有这个项目未完成的任务或者此项目有任务正在执行，则丢弃
            return Constant.FAIL;
        }
        Task task = new Task();
        task.setProjectID(project.getProjectID());
        task.setChecker(project.getChecker());
        task.setCompeletedFlag(0);
        task.setState("排队中");
        task.setStartTime(System.currentTimeMillis());
        taskService.addTask(task);
        // 如果项目表中没有此项目对应的任务，但此时有任务正在执行，则把此项目的新建任务放到到任务队列中，跟新此项目状态为等待
        if (runningThread != null && runningThread.isAlive()) {
            updateProjectState(project.getProjectID(), "等待");
            return Constant.TASK_WAITING;
        } else {
            tryScanTask();
        }
        return Constant.SUCCESS;
    }

    //尝试运行任务，先来先服务算法
    public synchronized int tryScanTask() {
        if (runningThread != null && runningThread.isAlive()) { //有任务正在运行
            return Constant.TASK_WAITING;
        }
        Task task = taskService.getFirstUnfinTask();//没有任务在运行，获得队首未执行任务执行
        if (task == null) { //如果任务队列为空，则跳过本次循环
            return Constant.TASK_QUEUE_EMPTY;
        }
        String cmd = buildRunScanTaskCmd(task.getProjectID(), task.getTaskID());// 构造任务执行的参数
        LogUtil.taskCmdLog("项目编号：" + task.getProjectID() + "任务编号：" + task.getTaskID() + "任务命令: " + cmd);
        runningThread = new RunTaskThread(task, cmd, netConfService.getEth0Bandwidth());//启动任务执行线程
        runningThread.start();
        taskService.recordStartTime(task.getTaskID(), System.currentTimeMillis());//记录任务开始执行的时间
        return Constant.SUCCESS;
    }

    //构造任务运行参数传递给控制模块
    public String buildRunScanTaskCmd(int projectid, int taskid) {
        String cmd = "";//运行子软件的命令
        //获取任务控制子模块的路径
        InputStream inputStream = TaskSchedueModule.class.getClassLoader().getResourceAsStream("subcontroller.properties");
        Properties p = new Properties();
        try {
            p.load(inputStream);
            cmd = p.getProperty("subcontroller");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Project project = projectService.getProjectByID(projectid);
        //获取项目要扫描的网段
        if (!project.getTarget().equals("")) {
            cmd += " " + "--HD-target" + " " + project.getTarget();
        }
        //主机发现指定要扫描的端口
        if (!project.getHdAdditionPort().equals("")) {
            cmd += " " + "--HD-addition-port" + " " + project.getHdAdditionPort();
        }
        //主机发现扫描强度等级
        cmd += " " + "--HD-intensity" + " " + project.getIntensity();
        //主机发现发送探送包使用的协议
        cmd += " " + "--HD-protocol" + " " + project.getProtocol();
        //指定扫描的黑名单
        if (!project.getBlackList().equals("")) {
            cmd += " " + "--HD-blacklist" + " " + project.getBlackList();
        }
        //开启拓扑发现
        if (project.getEnableTopoProbe() == 1) {
            cmd += " " + "--HD-enable-topo-probe";
        }
        //端口扫描使用的扫描类型
        cmd += " " + "--SD-scan-type" + " " + project.getScanType();
        //指定要扫描的具体端口
        if (!project.getSdAdditionPortTcp().equals("") && !project.getSdAdditionPortUdp().equals("")) {
            cmd += " " + "--SD-addition-port" + " " + "T:" + project.getSdAdditionPortTcp() + " " + "U:" + project.getSdAdditionPortUdp();
        }
        if (!project.getSdAdditionPortTcp().equals("") && project.getSdAdditionPortUdp().equals("")) {
            cmd += " " + "--SD-addition-port" + " " + "T:" + project.getSdAdditionPortTcp();
        }
        if (!project.getSdAdditionPortUdp().equals("") && project.getSdAdditionPortTcp().equals("")) {
            cmd += " " + "--SD-addition-port" + " " + "U:" + project.getSdAdditionPortUdp();
        }
        //端口扫描使用的端口数目
        cmd += " " + "--SD-top-ports" + " " + project.getTopPorts();
        //指定无需扫描的端口
        if (!project.getExcludePorts().equals("")) {
            cmd += " " + "--SD-exclude-ports" + " " + project.getExcludePorts();
        }
        //开启服务版本探测
        if (project.getEnableVersionDetec() == 1) {
            cmd += " " + "--SD-enable-version-detec";
        }
        //服务版本探测强度
        cmd += " " + "--SD-version-intensity" + " " + project.getVersionIntensity();
        //开启操作系统信息探测
        if (project.getEnableOsDetec() == 1) {
            cmd += " " + "--SD-enable-os-detec";
        }
        //onvif
        cmd += " " + "--OD-project_name" + " " + project.getProjectName();//要进行扫描的项目
        cmd += " " + "--OD-task_id" + " " + taskid;//扫描项目的ID
        //设备识别
        cmd += " " + "--DR-project_name" + " " + project.getProjectName();//要进行扫描的项目
        cmd += " " + "--DR-task_id" + " " + taskid;//扫描项目的ID
        //弱口令探测要探测的目标协议
        if (!project.getProbeModule().equals("")) {
            cmd += " " + "--WP-probe-module" + " " + project.getProbeModule();
        }
        //漏洞是否重启
        cmd += " " + "--VS-enable_reboot" + " " + project.getEnableReboot();
        //漏洞是否修改
        cmd += " " + "--VS-enable_change" + " " + project.getEnableChange();
        //系统带宽
        int bandwith = netConfService.getEth0Bandwidth();
        cmd += " " + "--SC-bandwidth_init" + " " + bandwith;
        return cmd;
    }

    //处理任务执行的结果
    public boolean handleTaskResult(int taskid, int projectid, String taskResult) {
        try {
            if (taskResult.indexOf("@end") == 0) {
                taskService.updateTaskError(taskid, projectid, strError);
                strError = "";
                return true;
            }
            if (taskResult.indexOf("@error") == 0) {
                String errorResult = taskResult.substring(6);
                JSONObject resultJson = JSONObject.parseObject(errorResult);//把结果转化为json对象
                int num = resultJson.getIntValue("NO");
                String msg = resultJson.getString("msg");
                strError = num + msg + "#";
                if (resultJson.getIntValue("NO") >= 300) {
                    injectCmd("stop");
                }
            }
            if (taskResult.indexOf("@output") == 0) {
                String resultStr = taskResult.substring(7);//取出任务执行结果
                JSONObject resultJson = JSONObject.parseObject(resultStr);//把结果转化为json对象
                if (resultJson.containsKey("progress")) {//如果是任务进度
                    int progress = resultJson.getIntValue("progress");
                    String process = resultJson.getString("process");
                    taskService.updateTaskProgress(taskid, progress, process);
                } else {
                    String process = resultJson.getString("process");
                    JSONObject ipJson = resultJson.getJSONObject("IP_info");
                    taskResultService.saveTaskResult(taskid, projectid, process, ipJson);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    //向正在执行任务的标注输出流中写入前端发来命令，输出到控制子模块
    public void injectCmd(String cmd) {
        runningThread.injectCmd(cmd);
    }

    //更新任务的状态
    public void updateTaskState(int taskid, String state) {
        taskService.updateTaskState(taskid, state);
    }

    //更新任务对应的项目的状态
    public void updateProjectState(int projectID, String state) {
        projectService.updateProjectState(projectID, state);
    }

    //更新任务执行完毕标识(正常结束或者出错)
    public void setTaskEndFlag(int taskID) {
        taskService.setTaskEndFlag(taskID);
    }

    //更新任务的耗时
    public void updateTaskTakingTime(int taskID, long takingTime) {
        taskService.updateTaskTakingTime(taskID, takingTime);
    }

    //更新项目的任务数
    public void addProjectTaskNum(int projectID) {
        projectService.plusProjectTaskNum(projectID);
    }

    //任务执行消耗的网络带宽
    public void updateTaskUsedFlow(int taskID, double curRate) {
        taskService.updateTaskUsedFlow(taskID, curRate);
    }

    //生成任务报表
    public void generateReport(int taskID, int projectID) {
        ReportResultCount reportResultCount = reportService.getReportResultCount(projectID, taskID);
        Project project = projectService.getProjectByID(projectID);
        String proName=project.getProjectName();
        String reportPath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/docs")
                + "/" + proName + "/" + taskID + "-" + reportResultCount.getTaskNo() + "-" + reportResultCount.getCheckerDate();
        String reportUrl = reportPath + ".pdf";
        boolean flag = new ReportUtil(projectID, taskID).manipulatePdf(reportUrl);
        if (flag) {
            reportUrl = reportUrl.substring(reportPath.indexOf("/electric"));
            taskService.updateUrl(taskID, reportUrl);
        }
    }
}