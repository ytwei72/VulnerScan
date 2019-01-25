package com.dky.vulnerscan;

import com.dky.vulnerscan.dao.UserDao;
import com.dky.vulnerscan.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.function.Consumer;

@SpringBootApplication
public class VulnerScanApplication {

    public static void main(String[] args) {

        SpringApplication.run(VulnerScanApplication.class, args);
    }

}

