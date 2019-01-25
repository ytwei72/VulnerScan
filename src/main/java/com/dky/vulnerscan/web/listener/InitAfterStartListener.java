package com.dky.vulnerscan.web.listener;

import com.dky.vulnerscan.service.ProjectService;
import com.dky.vulnerscan.service.TaskService;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by bo on 2017/3/23.
 */
public class InitAfterStartListener implements ServletContextListener {
    WebApplicationContext context;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        context = ContextLoader.getCurrentWebApplicationContext();
        TaskService taskService = (TaskService) context.getBean("taskService");
        ProjectService projectService = (ProjectService) context.getBean("projectService");
        taskService.endUnfinishedTask();
        projectService.endUnfinishedProject();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

}
