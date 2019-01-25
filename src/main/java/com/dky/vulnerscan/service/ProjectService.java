package com.dky.vulnerscan.service;


import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import com.dky.vulnerscan.module.TaskSchedueModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ContextLoader;

import com.dky.vulnerscan.dao.ProjectDao;
import com.dky.vulnerscan.entity.Project;
import com.dky.vulnerscan.entity.User;
import com.dky.vulnerscan.entityview.PageNation;
import com.dky.vulnerscan.util.Constant;
import com.dky.vulnerscan.util.DelFilesUtil;

@Service
public class ProjectService extends BaseService {
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskResultService taskResultService;
    @Autowired
    private TaskSchedueModule taskSchedueModule;

    public PageNation getProjectPageNation(int page, int perPage) {
        int allNum = projectDao.getAllProjectNum();
        return super.getPageNation(allNum, page, perPage);
    }

    public PageNation getProjectTaskPageNation(int projectID, int page, int perPage) {
        int projectTaskNum = projectDao.getProjectTaskNum(projectID);
        return super.getPageNation(projectTaskNum, page, perPage);
    }

    //获取指定页的项目列表
    public List<Project> getPageProjectList(int page, int perPageNum, User user) {
        int begin = (page - 1) * perPageNum;
        int offset = perPageNum;
        //检查员登陆后只能看到自己创建的项目，管理员登陆可以看到所有的项目
        String userName = user.getUserType().equals("admin") ? "%" : user.getUserName();
        return projectDao.getPageProjectList(begin, offset, userName);
    }

    //获取项目的总数
    public int getAllProjectNum() {
        return projectDao.getAllProjectNum();
    }

    //删除指定项目(删除项目的同时删除项目对应的所有任务、任务结果、报表)
    public int deleteProject(int projectID) {
        if (taskSchedueModule.getRunningThread() == null || taskSchedueModule.getRunningThread().getTask().getProjectID() != projectID) {
            Project project = getProjectByID(projectID);
            String url = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/docs") + "/" + project.getProjectName();
            DelFilesUtil.clearFiles(url);
            projectDao.deleteProject(projectID);
            taskService.deleteProjectTask(projectID);
            taskResultService.deleteProjectTaskResult(projectID);
            return Constant.SUCCESS;
        }
        return Constant.FAIL;  //正在执行状态的项目不能删除
    }

    // 获取项目表中的周期项目
    public List<Project> getPeriodicProject() {
        return projectDao.getPeriodicProject();
    }

    // 更新项目下次执行的时间(项目下次执行的时间+周期间隔,周期按天为单位)
    public void updateNextTime(int projectId) {
        Project project = projectDao.getProjectByID(projectId);
        long nextTime = project.getNextTime() + project.getSpace() * 60 * 60 * 24 * 1000;
        projectDao.updateNextTime(nextTime, projectId);
    }

    // 通过项目ID，查询项目
    public Project getProjectByID(int projectId) {
        return projectDao.getProjectByID(projectId);
    }

    //创建一个新项目
    public int createProject(Project project, HttpServletRequest request) {
        project.setBlackList("");// 数据库中只保存黑名单的绝对路径
        User user = (User) request.getSession().getAttribute(Constant.USER_CONTEXT);
        project.setChecker(user.getUserName());
        project.setTarget(formatTarget(project.getTarget()));//对目标ip的格式化处理
        project.setIntensity(1);
        project.setProtocol("tcp");
        project.setHdAdditionPort("");
        project.setEnableTopoProbe(0);
        project.setScanType("sS");
        project.setSdAdditionPortTcp("");
        project.setSdAdditionPortUdp("");
        project.setTopPorts(50);
        project.setExcludePorts("");
        project.setEnableVersionDetec(0);
        project.setVersionIntensity(3);
        project.setEnableOsDetec(1);
        project.setProbeModule("ssh ftp rtsp telnet");
        project.setEnableReboot(0);
        project.setEnableChange(1);
        project.setCreateTime(System.currentTimeMillis());
        project.setTime(System.currentTimeMillis());
        project.setState("等待");
        long nextTime = project.getProjectFlag() == 0 ? -1 : System.currentTimeMillis() + (project.getSpace() * Constant.UNIX_DAY_TIME);
        project.setNextTime(nextTime);
        projectDao.addProject(project);// 获取返回的自增主键，作为项目ID
        return Constant.SUCCESS;
    }

    //新建项目时判断是否已经有同名的项目存在
    public boolean hasProjectAlreadyExist(String projectName) {
        return projectDao.checkProjectAlreadyExist(projectName) <= 0;
    }

    //更新项目状态
    public void updateProjectState(int projectID, String state) {
        projectDao.updateProjectState(projectID, state);
    }

    // 对配置页面的弱口令复选框参数的处理
    private String formatProbeModule(String probe_module) {
        String protocol = "";
        if (probe_module != null) {
            Pattern p = Pattern.compile(",");
            Matcher m = p.matcher(probe_module);
            protocol = m.replaceAll(" ");
        }
        return protocol;
    }

    // 处理主机发现时指定的额外的端口
    private String formatAdditionPort(String additionPort) {
        String port = "";
        if (additionPort != null) {
            Pattern p = Pattern.compile("(\\s|\t|\r)+");
            Matcher m = p.matcher(additionPort);
            port = m.replaceAll(" ");
        }
        return port;
    }

    // 格式化目标IP
    private String formatTarget(String target) {
        String formatIPs = "";// 格式化之后的ip，多个参数使用空格隔开
        if (target != null) {
            Pattern p = Pattern.compile("(\\s|\t|\r|\n)+");
            Matcher m = p.matcher(target);
            formatIPs = m.replaceAll(" ");
        }
        return formatIPs;
    }

    //项目的任务数量加1
    public void plusProjectTaskNum(int projectID) {
        projectDao.plusProjectTaskNum(projectID);
    }

    //项目对应的任务数量减1
    public void reduceProjectTaskNum(int projectID) {
        //在删除任务时，设置对应项目的执行次数减1.
        int taskExecNum = getProjectTaskExecNum(projectID);
        if(taskExecNum > 0){
            projectDao.reduceProjectTaskNum(projectID);
        }
    }

    //获取项目对应的任务执行次数
    public int getProjectTaskExecNum(int projectID) {
        return projectDao.getProjectTaskNum(projectID);
    }

    //删除检查员对应的项目
    public void deleteCheckerProject(String checkerName) {
        projectDao.deleteCheckerProject(checkerName);
    }

    public void endUnfinishedProject() {
        projectDao.endUnfinishedProject();
    }
}
