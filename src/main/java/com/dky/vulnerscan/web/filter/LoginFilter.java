package com.dky.vulnerscan.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dky.vulnerscan.entity.User;
import com.dky.vulnerscan.util.Constant;

public class LoginFilter implements Filter {
	private String[] excludedUrls;

	@Override
	public void init(FilterConfig config) throws ServletException {
		String excludes = config.getInitParameter("excludedUrls");
		if (excludes != null) {
			this.excludedUrls = excludes.split(",");
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		String targetUrl = httpRequest.getRequestURI(); 

		//登录与否都可以访问的公共部分
		for (String url : excludedUrls) {
			if (targetUrl.contains(url.trim())) {
				chain.doFilter(request, response);
				return;
			}
		}
		//如果用户初次登录或者登录失败跳转到登录页面
		if(getSessionUser(httpRequest) == null || getSessionUser(httpRequest).getLoginState() < 0){
			httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
		}else{
			//如果是检查员初次登录，在没有填写真实姓名之前，不能访问其他页面
			if(getSessionUser(httpRequest).getUserType().equals("admin")){
				if (getSessionUser(httpRequest).getRealName().equals("") && !targetUrl.endsWith("/config/user") && !targetUrl.endsWith("/logout") && !targetUrl.endsWith("/changePs")){
					httpResponse.sendRedirect(httpRequest.getContextPath() + "/config/user");
				}
			}
			chain.doFilter(request, response);
			return;
		}
	}

	@Override
	public void destroy() {
		
	}
	
	protected User getSessionUser(HttpServletRequest request) {
		return (User) request.getSession().getAttribute(Constant.USER_CONTEXT);
	}
}
