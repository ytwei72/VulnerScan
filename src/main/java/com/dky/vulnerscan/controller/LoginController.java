package com.dky.vulnerscan.controller;

import com.dky.vulnerscan.entity.User;
import com.dky.vulnerscan.service.LoginService;
import com.dky.vulnerscan.service.UserService;
import com.dky.vulnerscan.util.Constant;
import com.dky.vulnerscan.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LoginController extends BaseController {
    @Autowired
    private UserService userService;
    @Autowired
    private LoginService loginService;

    //用户登录
    @RequestMapping(value = {"/","/login"})
    public String loginPage(HttpServletRequest request, ModelMap modelMap) {
        String bizMsg = "";
        int bizNo = 1;
        int leftTryLoginTimes = 0;
        User user = getSessionUser(request);
        if (user == null) { // 用户首次登录
            user = new User();
            user.setLoginState(Constant.USER_FIRST_LOGIN);
            leftTryLoginTimes = Constant.MAX_LOGIN_NUM;
        } else {
            int loginState = user.getLoginState();
            if (loginState > 0) { // 如果登录成功后再刷新登录页面，跳转到主页面
                return "redirect:/project/project_list";
            }
            if (loginState == Constant.USER_NOT_EXIST) {
                leftTryLoginTimes = Constant.MAX_LOGIN_NUM;
                bizMsg = "用户不存在";
            } else if (loginState == Constant.USER_PASSWD_ERROR) {
                leftTryLoginTimes = user.getLeftTryNum();
                bizMsg = "密码错误,您还剩" + leftTryLoginTimes + "次尝试机会！";
            } else if (loginState == Constant.USER_LOCKED) {
                leftTryLoginTimes = 0;
                bizMsg = "用户被锁定，请" + Constant.LOGIN_INTERVAL / (1000 * 60) + "分钟之后再次尝试登录";
            }
            bizNo = loginState;
        }
        String mask = MD5Util.getRandomString(5);
        user.setMd5RandomKey(mask);
        modelMap.addAttribute("bizNo", bizNo);
        modelMap.addAttribute("bizMsg", bizMsg);
        modelMap.addAttribute("letfTryTimes", leftTryLoginTimes);
        modelMap.addAttribute("mask", mask);
        setSessionUser(request, user);// 把随机产生的md5的加密随机数保存到session中
        return "/login/login";
    }

    //用户登出
    @RequestMapping(value = "/logout")
    public String logout(HttpServletRequest request) {
        removeSessionUser(request);
        return "redirect:/login";
    }

    // 检查登录用户的用户名、密码是否与数据库中的一致
    @RequestMapping(value = "/login_check", method = RequestMethod.POST)
    public String loginCheck(HttpServletRequest request,
                             @RequestParam("usertype") String userType,
                             @RequestParam("username") String userName,
                             @RequestParam("password") String passwd) {
        int loginState = userService.checkLoginUser(userType, userName, passwd, getSessionUser(request).getMd5RandomKey());
        User user = new User();
        if (loginState < 0) {
            if (loginState != Constant.USER_NOT_EXIST && loginState != Constant.USER_LOCKED) {
                loginService.loginFail(userName);
                user = userService.getUserByUserName(userName);
                if(user.getLeftTryNum() == 0){
                    loginState = Constant.USER_LOCKED;
                }
            }
            user.setLoginState(loginState);
            setSessionUser(request, user);
            return "redirect:/login";
        } else {
            loginService.loginSuccess(userName);
            user = userService.getUserByUserName(userName);
            user.setLoginState(loginState);
            setSessionUser(request, user);
            return "redirect:/project/project_list";
        }
    }

}
