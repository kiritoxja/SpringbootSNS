package com.xja.springbootsns.model;

import org.springframework.stereotype.Component;

/**
 * 保存所有线程的登录用户信息
 **/
@Component
public class LoginUser {
    private static ThreadLocal<User> user = new ThreadLocal<>();

    public User getUser(){return user.get();}

    public void setUser(User user){
        LoginUser.user.set(user);}

    public void clearUsers(){
        user.remove();}
}
