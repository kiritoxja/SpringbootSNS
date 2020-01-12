package com.xja.springbootsns;

import com.xja.springbootsns.dao.UserDao;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(value = "com.xja.springbootsns.dao")
public class SpringbootsnsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootsnsApplication.class, args);
    }
}
