package com.dky.vulnerscan.module;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.dky.vulnerscan.entity.Task;
import com.dky.vulnerscan.util.LogUtil;
import com.dky.vulnerscan.util.NetUsageThread;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

public class RunTaskThread extends Thread {
    private Task task;// 正在执行的任务
    private BufferedWriter bw = null;
    private BufferedReader br = null;
    private boolean recvEndCmd = false;//是否收到@end标识
    private double bandwidth = 0;
    private long lastTime = 0;
    private NetUsageThread netUsed = null;

    private WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
    private TaskSchedueModule taskSchedueModule = (TaskSchedueModule) context.getBean("TaskSchedueModule");

    public Task getTask() {
        return task;
    }


    public RunTaskThread(Task task, String cmd, double bd) {
        Runtime rt = Runtime.getRuntime();
        this.task = task;
        this.bandwidth = bd;
        try {
            //调用控制子软件执行任务并且捕获其标准输入、输出、错误流
            Process pr = rt.exec(new String[]{"/bin/sh", "-c", cmd});
            InputStream inputStream = pr.getInputStream();
            OutputStream outPutStream = pr.getOutputStream();
            br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            bw = new BufferedWriter(new OutputStreamWriter(outPutStream, "UTF-8"));

            taskSchedueModule.updateTaskState(task.getTaskID(), "正在执行");// 更新任务的状态为正在执行
            taskSchedueModule.updateProjectState(task.getProjectID(), "正在执行");
            taskSchedueModule.addProjectTaskNum(task.getProjectID());
            startNetUsageThread();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 启动计算网络剩余带宽线程，并把计算的结果写入到任务执行线程的标注输出流中
    private void startNetUsageThread() {
        netUsed = new NetUsageThread(task, bandwidth, bw);// 捕获执行任务的输出流
        netUsed.start();
    }

    @Override
    public void run() {
        try {
            String result = null;
            long startTime = System.currentTimeMillis();
            while ((result = br.readLine()) != null && !recvEndCmd) {
                LogUtil.taskResultLog("项目编号：" + task.getProjectID() + "任务编号: " + task.getTaskID() + "结果： " + result);
                updateTaskTakingTime(task.getTaskID(), startTime);//更新任务执行耗时
                if (result.indexOf("@") == 0) {
                    if (!recvEndCmd) { //没有收到@end对结果进行处理
                        recvEndCmd = taskSchedueModule.handleTaskResult(task.getTaskID(), task.getProjectID(), result);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (recvEndCmd) {
                taskSchedueModule.updateTaskState(task.getTaskID(), "完成");
            } else {
                taskSchedueModule.updateTaskState(task.getTaskID(), "异常结束");
            }
            taskSchedueModule.updateProjectState(task.getProjectID(), "空闲"); //任务结束后把其对应的项目置为空闲
            taskSchedueModule.generateReport(task.getTaskID(),task.getProjectID());  //生成报表
            taskSchedueModule.setTaskEndFlag(task.getTaskID());// 设置任务是否执行完成状态设置为1标识执行完
            taskSchedueModule.setRunningThread(null);
            taskSchedueModule.tryScanTask();// 每次任务执行完，立马尝试执行下一个，而不是等轮训
        }
    }

    //向正在执行的任务中注入命令（例如：stop等）
    public void injectCmd(String cmd) {
        if (bw != null) {
            synchronized (bw) {
                try {
                    bw.write("@input{\"cmd\":\"" + cmd + "\"}" + "\n");
                    bw.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        bw.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    bw = null;
                }
            }
        }
    }

    // 更新任务的执行耗时
    public void updateTaskTakingTime(int taskID, long startTime) {
        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastTime) / 1000 > 1) {
            taskSchedueModule.updateTaskTakingTime(taskID, (currentTime - startTime) / 1000);
            lastTime = currentTime;
        }
    }
}