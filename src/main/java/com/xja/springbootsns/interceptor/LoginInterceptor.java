package com.xja.springbootsns.interceptor;

import com.xja.springbootsns.model.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *如果用户没有登录  则跳转登录页 并在url记录要访问的页面
 **/
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    LoginUser loginUser;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        if(loginUser.getUser() == null ){
            //未登录 跳转登录并带上之前要访问的路径
            httpServletResponse.sendRedirect("/reglogin?next="+httpServletRequest.getRequestURI());
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }
}
