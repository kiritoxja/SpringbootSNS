package com.xja.springbootsns.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *测试controller为单例 多个请求修改成员变量会有问题
 **/
@Controller
public class TestController {

    private int a = 0;

    @RequestMapping("/test")
    @ResponseBody
    public String test(){
        a++;
        return String.valueOf(a);
    }

}
