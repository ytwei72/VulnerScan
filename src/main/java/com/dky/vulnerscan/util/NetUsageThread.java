package com.dky.vulnerscan.util;

import com.dky.vulnerscan.entity.Task;
import com.dky.vulnerscan.module.TaskSchedueModule;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class NetUsageThread extends Thread {
    private Task task;
    private double TotalBandwidth = 0;
    private BufferedWriter bw = null;
    private WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
    private TaskSchedueModule taskSchedueModule = (TaskSchedueModule) context.getBean("TaskSchedueModule");

    public NetUsageThread(Task task, double totalBandwidth, BufferedWriter bw) {
        this.task = task;
        this.TotalBandwidth = totalBandwidth * 1024;//以kb为单位
        this.bw = bw;
    }

    //计算网络带宽使用量
    private double getCurRate() {
        String command = "cat /proc/net/dev";
        // 第一次采集流量数据
        long startTime = System.currentTimeMillis();
        long firstFlow = calculationFlow(command);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println("NetUsage休眠时发生InterruptedException. " + e.getMessage());
        }
        // 第二次采集流量数据
        long endTime = System.currentTimeMillis();
        long secondFlow = calculationFlow(command);
        double interval = (endTime - startTime) * 1.0 / 1000;
        // 网口传输速度,单位为kbs
        return (secondFlow - firstFlow) * 8 / (1000 * interval);
    }

    private long calculationFlow(String command) {
        Runtime r = Runtime.getRuntime();
        String line = null;
        Process pro = null;
        BufferedReader in = null;
        long inSize = 0, outSize = 0;
        try {
            pro = r.exec(command);
            in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("eth0")) {
                    line = line.substring(5).trim();
                    String[] temp = line.split("\\s+");
                    inSize = Long.parseLong(temp[0].trim()); // Receive// bytes,单位为Byte
                    outSize = Long.parseLong(temp[8].trim());// Transmit// bytes,单位为Byte
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                pro.destroy();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return inSize + outSize;
    }

    public void run() {
        String bandwidth_rest = "";
        try {
            while (true) {
                Thread.sleep(1000);
                double usedFlow = getCurRate();
                bandwidth_rest = "@input{\"bandwidth_rest\":" + (TotalBandwidth - usedFlow) + "}";
                //更新任务运行时使用的流量
                taskSchedueModule.updateTaskUsedFlow(task.getTaskID(), usedFlow);
                if (bw != null) {
                    synchronized (bw) {
                        bw.write(bandwidth_rest + '\n');
                        bw.flush();
                    }
                } else {
                    break;
                }
            }
        } catch (IOException | InterruptedException e) {
            taskSchedueModule.updateTaskUsedFlow(task.getTaskID(), 0);
            e.printStackTrace();
        }
    }

}