package com.xja.springbootsns.controller;

import com.xja.springbootsns.model.LoginUser;
import com.xja.springbootsns.service.serviceInterface.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.TreeMap;


/**
 *登录 注册控制器
 **/
@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    public static int tokenSavedTime = 3600*24*5;

    @Autowired
    UserService userServiceImpl;

    @Autowired
    LoginUser loginUser;

    @PostMapping(value = "/login")
    public String login(String username, String password,String remember,
                        @RequestParam(value = "next", required = false) String next, Model model, HttpServletResponse response) {
        try {
            Map<String,Object> resultMap = userServiceImpl.login(username, password);
            if(resultMap.containsKey("token")) {
                //登录成功 发送JWT票据 记住登录状态
                String token = (String) resultMap.get("token");
                Cookie cookie = new Cookie("token", token);
                cookie.setPath("/");
                //如果选了remember 就长时间保存  否则应该视为session cookie
                // 但chrome有bug 没有setMaxAge还是会长时间保存 就保存1s cookie.setMaxAge(1); 但这样会让后续无法判断是否登录了 还是放弃了
                // 后来又发现chrome修好了 不setMaxAge 关了浏览器后cookie会自动销毁了
                if(!StringUtils.isEmpty(remember)) {
                    cookie.setMaxAge(tokenSavedTime);
                }
                cookie.setHttpOnly(true);
                response.addCookie(cookie);
                if(!StringUtils.isEmpty(next)){
                    return "redirect:" + next;
                }
                return "redirect:/index.html";
            }else {
                model.addAttribute("loginMessage", resultMap.get("msg"));
                return "login";
            }
        } catch (Exception e) {
            logger.error("登录异常");
            e.printStackTrace();
            model.addAttribute("loginMessage", "服务器错误");
            return "login";
        }
    }

    //未登录用户跳转注册登录页 并记录之前要访问的页面
    @RequestMapping("/reglogin")
    public String relogin( @RequestParam(value = "next", required = false) String next , Model model){
        model.addAttribute("next", next);
        return "login";
    }

    @PostMapping(value = {"/register"})
    public String register(String username, String password,  @RequestParam(value = "next", required = false) String next,Model model, HttpServletResponse response){
        try {
            //注册用户 并返回token
            String token = userServiceImpl.register(username, password).get("token").toString();
            Cookie cookie = new Cookie("token", token);
            cookie.setMaxAge(tokenSavedTime);
            response.addCookie(cookie);
            if(!StringUtils.isEmpty(next)){
                return "redirect:" + next;
            }
            return "redirect:/index.html";
        } catch (Exception e) {
            logger.error("登录异常");
            e.printStackTrace();
            model.addAttribute("registerMessage", "服务器错误");
            return "register";
        }
    }

    @RequestMapping("/logout")
    public String logout(HttpServletResponse response){
        //删除cookie 和 loginuser中的用户登录信息
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        loginUser.clearUsers();
        return "redirect:/";
    }

    @PostMapping(value = "/checkUserName")
    @ResponseBody
    public boolean checkUserName(String userName) {
        if( null != userServiceImpl.getUserByName(userName)) return true;
        else return false;
    }
}
