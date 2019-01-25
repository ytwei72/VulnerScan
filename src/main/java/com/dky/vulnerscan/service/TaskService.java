package com.dky.vulnerscan.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dky.vulnerscan.entityview.Param;
import com.dky.vulnerscan.module.TaskSchedueModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dky.vulnerscan.dao.TaskDao;
import com.dky.vulnerscan.entity.Task;
import com.dky.vulnerscan.entityview.TaskOverView;
import com.dky.vulnerscan.entityview.VulCount;
import com.dky.vulnerscan.util.Constant;
import com.dky.vulnerscan.util.DelFilesUtil;

@Service
public class TaskService extends BaseService {
    @Autowired
    private TaskDao taskDao;
    @Autowired
    private TaskResultService taskResultService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private TaskSchedueModule taskSchedueModule;

    //获取所有的任务数
    public int getAllTaskNum() {
        return taskDao.getAllTaskNum();
    }

    // 更新对应任务的错误信息
    public ArrayList<Map<String, String>> getTaskError(String taskError) {
        ArrayList<Map<String, String>> errorList = new ArrayList<>();
        if (taskError.length() > 0) {
            String[] errorArray = taskError
                    .substring(0, taskError.length() - 1).split("#");
            for (int i = 0; i < errorArray.length; i++) {
                Map<String, String> errorMap = new HashMap<>();
                int errorNO = Integer.parseInt(errorArray[i].substring(0, 3));
                if (errorNO / 100 == 1) {
                    errorMap.put("errorType", "common");
                } else if (errorNO / 100 == 2) {
                    errorMap.put("errorType", "mid");
                } else if (errorNO / 100 == 3) {
                    errorMap.put("errorType", "serious");
                }
                errorMap.put("errorMsg", errorArray[i].substring(3));
                errorList.add(errorMap);
            }
        }
        return errorList;
    }

    //更新对应任务的错误信息
    public void updateTaskError(int taskid, int projectid, String strError) {
        taskDao.updateTaskError(taskid, projectid, strError);
    }

    //删除项目对应的所有任务
    public void deleteProjectTask(int projectID) {
        taskDao.deleteProjectTask(projectID);
    }

    //获取指定项目对应的所有任务及统计任务的资产、漏洞数量
    public List<Task> getProjectTaskList(int projectID) {
        List<Task> taskList = taskDao.getProjectTaskList(projectID);
        for (int i = 0; i < taskList.size(); i++) {
            countTaskPropAndVul(taskList.get(i));
        }
        return taskList;
    }

    //统计指定项目对应的任务的资产数及漏洞数量
    public void countTaskPropAndVul(Task task) {
        int properties = taskResultService.getTaskPropertyNum(task.getTaskID(), task.getProjectID());
        VulCount vulCount = taskResultService.getTaskResultVulCount(task.getTaskID(), task.getProjectID());
        int counts = countTaskExecNum(task.getTaskID(),task.getProjectID()); //统计任务是第几次执行
        task.setProperties(properties);
        task.setVulCount(vulCount);
        task.setCounts(counts);
    }

    //统计任务是第几次执行
    public int countTaskExecNum(int taskID, int projectID) {
        return taskDao.getTaskCounts(taskID,projectID);
    }

    //获取项目指定页对应的任务
    public List<Task> getPageProjectTaskList(int projectID, int page, int perPage) {
        int begin = (page - 1) * perPage;
        int offset = perPage;
        List<Task> taskList = taskDao.getPageProjectTaskList(projectID, begin, offset);
        for (int i = 0; i < taskList.size(); i++) {
            countTaskPropAndVul(taskList.get(i));
        }
        return taskList;
    }

    //检测指定项目是否有未完成的任务(排队或者正在执行)
    public boolean hasUnfinTask(int projectid) {
        return taskDao.getProjectUnfinTaskNum(projectid) > 0;
    }

    //向任务表中添加一个任务
    public void addTask(Task task) {
        taskDao.addTask(task);
    }

    // 从任务表中获取的入队最早的任务
    public Task getFirstUnfinTask() {
        return taskDao.getFirstUnfinTask();
    }

    // 更新任务执行的状态
    //state: 只能是 异常结束、排队中、正在执行和完成
    //异常结束、排队中、正在执行时：直接更新；
    //完成：此时，如果iscancel被置1，更新为“手动停止”；如果，进度不为100%，更新为”异常结束“；如果进度为100%，更新为”完成“
    public void updateTaskState(int taskid, String state) {
        Task task = taskDao.getTaskByTaskID(taskid);
        int isCancel = task.getIscancel();
        int progress = task.getTotalProgress();
        if (state.equals("完成")) {
            if (progress < 100 && isCancel == 1) {
                taskDao.updateTaskState(taskid, "手动停止");
                return;
            }
            if (progress < 100) {
                taskDao.updateTaskState(taskid, "异常结束");
                return;
            }
            taskDao.updateTaskState(taskid, state);
        } else {
            taskDao.updateTaskState(taskid, state);
        }
    }

    // 更新任务执行的进度
    public void updateTaskProgress(int taskid, int progress, String process) {
        switch (process) {
            case "Total":
                process = "total_progress";
                break;
            case "HD": // 主机发现
                process = "hd_progress";
                break;
            case "SD": // 端口扫描
                process = "sd_progress";
                break;
            case "OD": // onvif
                process = "od_progress";
                break;
            case "DR": // 设备识别
                process = "dr_progress";
                break;
            case "WP": // 弱密钥检测
                process = "wp_progress";
                break;
            case "VS": // 漏洞验证
                process = "vs_progress";
                break;
        }
        taskDao.updateTaskProgress(taskid, progress, process);
    }

    // 更新执行任务的耗时
    public void updateTaskTakingTime(int taskid, long taking_time) {
        taskDao.updateTaskTakingTime(taskid, taking_time);
    }

    // 更新任务是否执行完成标识
    public void setTaskEndFlag(int taskID) {
        taskDao.setTaskEndFlag(taskID);
    }

    // 查询任务的状态
    public String getTaskState(int taskid) {
        return taskDao.getTaskState(taskid);
    }

    // 记录任务开始执行的时间
    public void recordStartTime(int taskid, long start_time) {
        taskDao.recordStartTime(taskid, start_time);
    }

    // 获取指定页项目对应的最后一次任务
    public Task getFinalTask(int projectid) {
        return taskDao.getFinalTask(projectid);
    }

    // 根据任务标号获取指定任务
    public Task getTaskByTaskID(int taskID) {
        return taskDao.getTaskByTaskID(taskID);
    }

    //获取任务的概览信息
    public TaskOverView getTaskOverView(int taskID, int projectID) {
        TaskOverView taskOverView = new TaskOverView();
        Task task = taskDao.getTaskByTaskID(taskID);
        int counts = taskDao.getTaskCounts(taskID, projectID);
        int deviceNum = taskResultService.getTaskPropertyNum(taskID, projectID);
        ArrayList<Param> progressList = getSubSoftProgress(task);//获取各个子软件进度
        ArrayList<Map<String, String>> errorList = new ArrayList<>();
        errorList = this.getTaskError(task.getTaskError());
        taskOverView.setState(task.getState());
        taskOverView.setDeviceNum(deviceNum);
        taskOverView.setStartTime(task.getStartTime());
        taskOverView.setCounts(counts);
        taskOverView.setTakingTime(task.getTakingTime());
        taskOverView.setProgress(progressList);
        taskOverView.setTotalProgress(task.getTotalProgress());
        taskOverView.setErrorList(errorList);
        taskOverView.setRealBandWidth(task.getUsedFlow());
        taskOverView.setReportFormPath(task.getReportUrl());
        return taskOverView;
    }

    //获取任务的所有子软件进度
    public ArrayList<Param> getSubSoftProgress(Task task) {
        ArrayList<Param> progressList = new ArrayList<>();
        Param hdParam = new Param();
        Param sdParam = new Param();
        Param drParam = new Param();
        Param vsParam = new Param();
        hdParam.setName("主机发现");
        hdParam.setValue(task.getHdProgress());
        sdParam.setName("服务检测");
        sdParam.setValue(task.getSdProgress());
        drParam.setName("设备识别");
        drParam.setValue(task.getDrProgress());
        vsParam.setName("漏洞扫描");
        vsParam.setValue(task.getVsProgress());
        progressList.add(hdParam);
        progressList.add(sdParam);
        progressList.add(drParam);
        progressList.add(vsParam);
        return progressList;
    }

    //删除任务（删除任务的同时删除任务对应的结果）
    public int deleteTask(int projectID, int taskID) {
        if (taskSchedueModule.getRunningThread() == null || taskSchedueModule.getRunningThread().getTask().getTaskID() != taskID) {
            String url = taskDao.getTaskByTaskID(taskID).getReportUrl();
            if (url != null && url.length() > 0) {
                DelFilesUtil.clearFiles(url);
            }
            taskDao.deleteTask(taskID);
            taskResultService.deleteTaskResult(taskID);
            projectService.reduceProjectTaskNum(projectID);
            return Constant.SUCCESS;
        }
        return Constant.FAIL; //正在执行状态的任务不能删除
    }

    //停止正在执行的任务
    public void stopRunningTask(int projectID, int taskID) {
        taskSchedueModule.injectCmd("stop");
        taskDao.setTaskCancelFlag(projectID, taskID);
    }

    //更新任务的url
    public void updateUrl(int taskid, String url) {
        taskDao.updateTaskReportUrl(taskid, url);
    }

    //获取指定项目对应的执行次数
    public int getProjectTaskCount(int projectID) {
        return taskDao.getProjectTaskCount(projectID);
    }

    //更新任务执行消耗的流量
    public void updateTaskUsedFlow(int taskID, double curRate) {
        taskDao.updateTaskUsedFlow(taskID,curRate);
    }

    //获取指定检查员对应的任务列表
    public ArrayList<Task> getCheckerTaskList(String checkerName) {
        return (ArrayList<Task>) taskDao.getCheckerTaskList(checkerName);
    }

    //删除检查员对应的任务
    public void deleteCheckerTask(String checkerName) {
        taskDao.deleteCheckerTask(checkerName);
    }

    public void endUnfinishedTask() {
        taskDao.endUnfinishedTask();
    }
}
