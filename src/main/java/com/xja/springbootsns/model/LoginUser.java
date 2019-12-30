package com.xja.springbootsns.model;

import org.springframework.stereotype.Component;

/**
 * 保存所有线程的登录用户信息
 **/
@Component
public class LoginUser {
    private static ThreadLocal<User> users = new ThreadLocal<>();

    public User getUser(){return users.get();}

    public void setUsers(User user){users.set(user);}

    public void clearUsers(){users.remove();}
}
