package com.dky.vulnerscan.web.interceptor;

import com.dky.vulnerscan.entity.User;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthorityInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(request.getSession().getAttribute("USER_CONTEXT") != null){
            User user = (User) request.getSession().getAttribute("USER_CONTEXT");
            if(!user.getUserType().equals("admin") && user.getEnableManageVulLib() == 0){
                response.sendRedirect(request.getContextPath() + "/project/project_list");
                return false;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
