package com.dky.vulnerscan.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.dky.vulnerscan.entityview.*;
import com.dky.vulnerscan.module.ExploitScanModule;
import com.dky.vulnerscan.module.OpenVasScanModule;
import com.dky.vulnerscan.module.TaskSchedueModule;
import com.dky.vulnerscan.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dky.vulnerscan.entity.Project;
import com.dky.vulnerscan.entity.Task;
import com.dky.vulnerscan.entity.User;
import com.dky.vulnerscan.service.ProjectService;
import com.dky.vulnerscan.service.TaskResultService;
import com.dky.vulnerscan.service.TaskService;

@Controller
@RequestMapping("/project")
public class ProjectController extends BaseController {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskResultService taskResultService;
    @Autowired
    private ExploitScanModule exploitScanModule;
    @Autowired
    private OpenVasScanModule openVasScanModule;
    @Autowired
    private TaskSchedueModule taskSchedueModule;
    @Autowired
    private EmailService emailService;

    //加载项目列表页面
    @RequestMapping(value = "/project_list")
    public String projectListPage(HttpServletRequest request, ModelMap model) {
        User user = super.getSessionUser(request);
        model.addAttribute("userType", user.getUserType());
        return "/project/project_list/project_list";
    }

    // 项目列表页列出所有项目的概览信息(分页)
    @RequestMapping(value = "/ajax_project_list", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getPageProjectList(HttpServletRequest request,
                                                  @RequestParam("page") int page,
                                                  @RequestParam("perPage") int perPage) {
        Map<String, Object> modelMap = new HashMap<>();
        User user = super.getSessionUser(request);
        PageNation pageNation = projectService.getProjectPageNation(page, perPage);
        List<Project> projectList = projectService.getPageProjectList(page, perPage, user);
        modelMap.put("bizNo", 1);
        modelMap.put("bizMsg", "");
        modelMap.put("userType", user.getUserType());
        modelMap.put("pagenation", pageNation);
        modelMap.put("projectList", projectList);
        return modelMap;
    }

    // 加载创建项目页面
    @RequestMapping(value = "/create_project")
    public String createProjectPage(ModelMap model, HttpServletRequest request) {
        int bizNo = super.getSessionState(request);
        model.put("bizNo", bizNo);
        super.removeSessionState(request);
        return "/project/create/create";
    }

    // 创建一个新项目
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String createProject(Project project, HttpServletRequest request) {
        int createState = -1;
        if (hasProjectAlreadyExist(project.getProjectName())) {
            if (project.getProjectName() != null && !project.getProjectName().equals("")
                    && project.getTarget() != null && !project.getTarget().equals("")) {
                createState = projectService.createProject(project, request);
            }
        }
        super.setSessionState(request, createState);
        if(createState > 0){
            taskSchedueModule.tryCreateTask(project);// 任务调度模块对于新建项目的处理
        }
        return "redirect:/project/create_project";
    }

    //新建项目的时候，判断项目是否存在
    @RequestMapping(value = "/project_exist")
    @ResponseBody
    public boolean hasProjectAlreadyExist(@RequestParam("name") String projectName) {
        return projectService.hasProjectAlreadyExist(projectName);
    }

    //删除项目
    @RequestMapping(value = "/delete_project")
    @ResponseBody
    public Map<String, Object> deleteProject(@RequestParam("projectID") int projectID) {
        Map<String, Object> modelMap = new HashMap<>();
        int bizNo = projectService.deleteProject(projectID);
        String msg = "";
        if (bizNo < 0) {
            msg = "正在执行的任务不能删除";
        }
        modelMap.put("bizNo", bizNo);
        modelMap.put("bizMsg", msg);
        return modelMap;
    }

    //加载项目详情页
    @RequestMapping(value = "/project_detail")
    public String projectDetailPage(
            @RequestParam("projectID") int projectID,
            HttpServletRequest request,
            ModelMap model) {
        User user = super.getSessionUser(request);
        model.addAttribute("userType", user.getUserType());
        model.addAttribute("projectID", projectID);
        model.addAttribute("projectName", projectService.getProjectByID(projectID).getProjectName());
        return "/project/project_detail/project_detail";
    }

    // 获取项目的配置信息
    @RequestMapping(value = "/project_config")
    @ResponseBody
    public Map<String, Object> getProjectConfig(@RequestParam("projectID") int projectID) {
        Map<String, Object> modelMap = new HashMap<>();
        Project project = projectService.getProjectByID(projectID);
        modelMap.put("bizNo", 1);
        modelMap.put("bizMsg", "");
        modelMap.put("content", project);
        return modelMap;
    }

    //手动执行项目
    @RequestMapping(value = "/exec_project")
    @ResponseBody
    public Map<String, Object> manualExecProject(@RequestParam("projectID") int projectID) {
        Map<String, Object> modelMap = new HashMap<>();
        Project project = projectService.getProjectByID(projectID);
        int execNo = taskSchedueModule.tryCreateTask(project);// 任务调度模块对于新建项目的处理
        modelMap.put("bizNo", 1);
        modelMap.put("execNo", execNo);
        modelMap.put("bizMsg", "");
        return modelMap;
    }

    //获取每个项目对应的所有任务列表
    @RequestMapping(value = "/task_list", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getProjectTaskList(@RequestParam("projectID") int projectID) {
        Map<String, Object> modelMap = new HashMap<>();
        List<Task> taskList = taskService.getProjectTaskList(projectID);
        modelMap.put("bizNo", 1);
        modelMap.put("bizMsg", "");
        modelMap.put("taskList", taskList);
        return modelMap;
    }

    // 获取每个项目对应的指定页数任务列表
    @RequestMapping(value = "/task_page_list", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getProjectPageTaskList(
            @RequestParam("projectID") int projectID,
            @RequestParam("page") int page, @RequestParam("perPage") int perPage) {
        Map<String, Object> modelMap = new HashMap<>();
        PageNation pageNation = projectService.getProjectTaskPageNation(projectID, page, perPage);
        List<Task> taskList = taskService.getPageProjectTaskList(projectID, page, perPage);
        modelMap.put("bizNo", 1);
        modelMap.put("bizMsg", "");
        modelMap.put("pagenation", pageNation);
        modelMap.put("taskList", taskList);
        return modelMap;
    }

    // 删除任务
    @RequestMapping(value = "/delete_task")
    @ResponseBody
    public Map<String, Object> deleteTask(
            @RequestParam("projectID") int projectID,
            @RequestParam("taskID") int taskID) {
        Map<String, Object> modelMap = new HashMap<>();
        int bizNo = taskService.deleteTask(projectID, taskID);
        String msg = "";
        if (bizNo < 0) {
            msg = "正在执行的任务不能删除";
        }
        modelMap.put("bizNo", bizNo);
        modelMap.put("bizMsg", msg);
        return modelMap;
    }

    // 停止正在执行的任务
    @RequestMapping(value = "/stop_task")
    @ResponseBody
    public Map<String, Object> stopRunningTask(
            @RequestParam("projectID") int projectID,
            @RequestParam("taskID") int taskID) {
        Map<String, Object> modelMap = new HashMap<>();
        taskService.stopRunningTask(projectID, taskID);
        modelMap.put("bizNo", 1);
        modelMap.put("bizMsg", "");
        return modelMap;
    }

    //加载任务详情页
    @RequestMapping(value = "/task_detail")
    public String taskDetailPage(@RequestParam("taskID") int taskID, ModelMap model) {
        Task task = taskService.getTaskByTaskID(taskID);
        String projectName = "";
        if (projectService.getProjectByID(task.getProjectID()) != null) {
            projectName = projectService.getProjectByID(task.getProjectID()).getProjectName();
        }
        model.addAttribute("taskID", taskID);
        model.addAttribute("projectID", task.getProjectID());
        model.addAttribute("projectName", projectName);
        return "/project/task_detail/task_detail";
    }

    @RequestMapping(value = "/ajax_task_detail")
    @ResponseBody
    public Map<String, Object> getTaskDetail(
            @RequestParam("taskID") int taskID,
            @RequestParam("projectID") int projectID) {
        Map<String, Object> modelMap = new HashMap<>();
        TaskOverView taskOverView = taskService.getTaskOverView(taskID, projectID);
        TaskResultCount taskResultCount = taskResultService.getTaskResultCount(taskOverView,
                taskID, projectID);
        modelMap.put("bizNo", 1);
        modelMap.put("bizMsg", "");
        modelMap.put("overview", taskOverView);
        modelMap.put("statistics", taskResultCount);
        return modelMap;
    }

    //设备列表
    @RequestMapping(value = "/device_list")
    @ResponseBody
    public Map<String, Object> getDeviceList(
            @RequestParam("taskID") int taskID,
            @RequestParam("projectID") int projectID,
            @RequestParam("page") int page,
            @RequestParam("perPage") int perPage,
            @RequestParam("searchKw") String searchKw,
            @RequestParam("sortKw") String sortKw,
            @RequestParam("sortStyle") String sortStyle) {
        Map<String, Object> modelMap = new HashMap<>();
        ArrayList<DeviceDetaiInfo> deviceList = taskResultService.getDeviceList(taskID, projectID, page,
                perPage, searchKw, sortKw, sortStyle);
        PageNation pageNation = taskResultService.getPageNation(taskID, projectID, page, perPage);
        modelMap.put("bizNo", 1);
        modelMap.put("bizMsg", "");
        modelMap.put("pagenation", pageNation);
        modelMap.put("deviceList", deviceList);
        return modelMap;
    }

    //设备详情
    @RequestMapping(value = "/device_detail")
    public String deviceDetailPage(
            @RequestParam("taskID") int taskID,
            @RequestParam("projectID") int projectID,
            @RequestParam("ip") String ip,
            ModelMap model) {
        DeviceDetaiInfo deviceDetailInfo = taskResultService.getDeviceDetailInfo(taskID, projectID, ip);
        model.addAttribute("ip", deviceDetailInfo.getIp());
        model.addAttribute("style", deviceDetailInfo.getStyle());
        model.addAttribute("brand", deviceDetailInfo.getBrand());
        model.addAttribute("service", deviceDetailInfo.getService());
        model.addAttribute("vulDes", deviceDetailInfo.getVulDes());
        model.addAttribute("projectID", projectID);
        model.addAttribute("taskID", taskID);
        return "/project/device_detail/device_detail";
    }

    //深度扫描
    @RequestMapping(value = "/deepVulScan")
    @ResponseBody
    public Map<String, Object> deepVulScan(
            @RequestParam("projectID") int projectID,
            @RequestParam("taskID") int taskID,
            @RequestParam("ip") String ip,
            @RequestParam("scanMethod") String scanMethod) {
        Map<String, Object> modelMap = new HashMap<>();
        int bizNo = 1;
        if (scanMethod.equals("exploit")) {
            bizNo = exploitScanModule.exploitDeepScan(projectID, taskID, ip);
        } else {
            bizNo = openVasScanModule.openVasDeepScan(projectID, taskID, ip);
        }
        modelMap.put("bizNo", bizNo);
        modelMap.put("bizMsg", "");
        return modelMap;
    }

    //请求设备漏洞列表
    @RequestMapping(value = "/vul_list")
    @ResponseBody
    public Map<String, Object> getDeviceVulList(
            @RequestParam("projectID") int projectID,
            @RequestParam("taskID") int taskID,
            @RequestParam("ip") String ip) {
        Map<String, Object> modelMap = new HashMap<>();
        ArrayList<VulDetail> vulList = taskResultService.getDeviceVulList(projectID, taskID, ip);
        modelMap.put("bizNo", 1);
        modelMap.put("bizMsg", "");
        modelMap.put("vulList", vulList);
        return modelMap;
    }

    //获取进行exploit漏洞验证的参数
    @RequestMapping(value = "/verify_parameter")
    @ResponseBody
    public Map<String, Object> getVertifyParam(
            @RequestParam("projectID") int projectID,
            @RequestParam("taskID") int taskID,
            @RequestParam("ip") String ip,
            @RequestParam("vulName") String vulName) {
        Map<String, Object> modelMap = new HashMap<>();
        //首先通过标准输入流写入@verify{"192.168.1.1":"windows/browser/sapgui_saveviewtosessionfile"}
        int bizNo = exploitScanModule.writeVertifyVulName(projectID, taskID, ip, vulName);
        ExploitVerifyParam param = taskResultService.getExploitVertifyParam(projectID, taskID, ip, vulName);
        modelMap.put("bizNo", bizNo);
        modelMap.put("bizMsg", "");
        modelMap.put("parameter", param);
        return modelMap;
    }

    //根据填写的参数进行exploit漏洞验证
    @RequestMapping(value = "/verify_vul")
    @ResponseBody
    public Map<String, Object> writeVertifyParam(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        exploitScanModule.writeExploitVertifyParams(request);
        modelMap.put("bizNo", 1);
        modelMap.put("bizMsg", "");
        return modelMap;
    }

    //是否有设备进行深度漏洞扫描
    @RequestMapping(value = "/vul_scan_state")
    @ResponseBody
    public Map<String, Object> getVulDeepScanState() {
        Map<String, Object> modelMap = new HashMap<>();
        HashMap<String, Integer> stateMap = new HashMap<>();
        if (openVasScanModule.isRunningOpenVasScan()) {
            stateMap.put("isOpenvasAvailable", -1);
        } else {
            stateMap.put("isOpenvasAvailable", 1);
        }
        if (exploitScanModule.isRunningExploitScan() || (exploitScanModule.isRunningExploitVertify() && !exploitScanModule.isHasReceivedParam())) {
            stateMap.put("isExploitAvailable", -1);
        } else {
            stateMap.put("isExploitAvailable", 1);
        }
        modelMap.put("bizNo", 1);
        modelMap.put("bizMsg", "");
        modelMap.put("stateMap", stateMap);
        return modelMap;
    }

    //获去exploit深度验证的结果
    @RequestMapping(value = "/check_verify_state")
    @ResponseBody
    public Map<String, Object> getDeepScanResult(
            @RequestParam("projectID") int projectID,
            @RequestParam("taskID") int taskID,
            @RequestParam("ip") String ip) {
        Map<String, Object> modelMap = new HashMap<>();
        int verifyState = -1;
        if (exploitScanModule.isRunningExploitVertify()) {
            verifyState = 1;
        }
        modelMap.put("bizNo", 1);
        modelMap.put("bizMsg", "");
        modelMap.put("verifyState", verifyState);
        return modelMap;
    }

    //发送任务报表
    @RequestMapping(value = "/sendForm")
    @ResponseBody
    public Map<String, Object> sendForm(@RequestParam("projectID") int projectID,
                                        @RequestParam("taskID") int taskID,
                                        HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        HashMap<String, Object> sendStateMap = emailService.sendEmail(taskID, getSessionUser(request).getUserName());
        modelMap.put("bizNo", sendStateMap.get("bizNo"));
        modelMap.put("bizMsg", sendStateMap.get("bizMsg"));
        return modelMap;
    }

}
