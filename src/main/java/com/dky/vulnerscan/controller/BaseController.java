package com.dky.vulnerscan.controller;

import javax.servlet.http.HttpServletRequest;

import com.dky.vulnerscan.entity.User;
import com.dky.vulnerscan.util.Constant;

public class BaseController {

    protected User getSessionUser(HttpServletRequest request) {
        return (User) request.getSession().getAttribute(Constant.USER_CONTEXT);
    }

    protected void setSessionUser(HttpServletRequest request, User user) {
        request.getSession().setAttribute(Constant.USER_CONTEXT, user);
    }

    protected void removeSessionUser(HttpServletRequest request) {
        request.getSession().removeAttribute(Constant.USER_CONTEXT);
    }

    protected int getSessionState(HttpServletRequest request) {
        if (request.getSession().getAttribute(Constant.CREATE_STATE) == null) {
            return 0;
        }
        return (int) request.getSession().getAttribute(Constant.CREATE_STATE);
    }

    protected void setSessionState(HttpServletRequest request, int state) {
        request.getSession().setAttribute(Constant.CREATE_STATE, state);
    }

    protected void removeSessionState(HttpServletRequest request) {
        request.getSession().removeAttribute(Constant.CREATE_STATE);
    }
}
