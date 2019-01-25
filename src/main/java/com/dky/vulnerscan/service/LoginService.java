package com.dky.vulnerscan.service;

import com.dky.vulnerscan.entity.User;
import com.dky.vulnerscan.util.Constant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    @Autowired
    private UserService userService;

    //用户登录失败的处理
    public void loginFail(String userName) {
        User user = userService.getUserByUserName(userName);
        long currentTime = System.currentTimeMillis();
        long interval = currentTime - user.getLastLoginTime();
        if (interval > Constant.LOGIN_INTERVAL) { //再次登录与上次登录的时间间隔大于指定间隔
            userService.updateUserLoginState(user.getUserName(), Constant.MAX_LOGIN_NUM - 1, currentTime);
        } else {
            if (user.getLeftTryNum() > 0) { //用户还没有被锁定,还有尝试机会
                userService.updateUserLoginState(user.getUserName(), user.getLeftTryNum() - 1, currentTime);
                user.setLeftTryNum(user.getLeftTryNum() - 1);
            }
        }
    }

    //用户登录成功后的处理
    public void loginSuccess(String userName) {
        long currentTime = System.currentTimeMillis();
        userService.updateUserLoginState(userName, Constant.MAX_LOGIN_NUM, currentTime);
    }

}
