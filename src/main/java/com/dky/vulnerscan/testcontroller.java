package com.dky.vulnerscan;


import com.dky.vulnerscan.dao.UserDao;
import com.dky.vulnerscan.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testcontroller {

    @Autowired
    private UserDao userDao;
    @RequestMapping(value="/test")
    String simpleTest() {
        User user = userDao.getUserByUserName("admin");
        System.out.println(user.getEmailName());
        return user.getUserName() + " Return home now.";
    }
}
