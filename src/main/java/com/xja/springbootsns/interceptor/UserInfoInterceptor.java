package com.xja.springbootsns.interceptor;

import com.xja.springbootsns.controller.LoginController;
import com.xja.springbootsns.model.LoginUser;
import com.xja.springbootsns.model.User;
import com.xja.springbootsns.service.serviceInterface.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 *用户信息拦截器  用于检查是否带有token 如果带有则记录用户信息 并刷新token
 * 并在返回视图前添加用户信息  方便视图获取   最后清除用户信息
 **/
@Component
public class UserInfoInterceptor implements HandlerInterceptor {

    double refreshToken = 0.25;

    @Autowired
    LoginUser loginUser;

    @Autowired
    UserService userServiceImol;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
       String token = null;
       Cookie[] cookies = request.getCookies();
       if(cookies != null){
           for(Cookie cookie : cookies){
               if(cookie.getName().equals("token")){
                   token = cookie.getValue();
                   break;
               }
           }
       }

       if(token != null){
           //检查token的合法性
           int status = userServiceImol.verifyToken(token);
           if(status > 0){
               User user = userServiceImol.getUserById(status);
               //如果token的有效期小于1/4有效时间 那么重新发布token
               Date refreshDate = new Date((long)(System.currentTimeMillis()+UserService.tokenExpiredTime * refreshToken));
               if(userServiceImol.getTokenExpiredDate(token).before(refreshDate)){
                   String newToken = userServiceImol.getToken(user);
                   Cookie cookie = new Cookie("token", newToken);
                   cookie.setMaxAge(LoginController.tokenSavedTime);
                   cookie.setHttpOnly(true);
                   response.addCookie(cookie);
               }
               loginUser.setUser(user);
           }
       }
       return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //为了让视图直接获取登录用户的信息 直接放入model  确保不是那种checkusername 没有返回视图的
       if(loginUser.getUser() != null && modelAndView != null){
           modelAndView.addObject("loginUser", loginUser.getUser());
       }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        loginUser.clearUsers();
    }
}
