package com.xja.springbootsns.service.serviceInterface;

import com.xja.springbootsns.exception.ExpiredJwtException;
import com.xja.springbootsns.model.User;

import java.util.Date;
import java.util.Map;

public interface UserService {

    int tokenExpiredTime = 1000*60*60*24 ;
    String tokenAlgorithm = "HS256";
    String tokenType = "JWT";

    User getUserById(int id);

    User getUserByName(String name);

    String getToken(User user);

    //验证成功返回userId
    int verifyToken(String token) ;

    Date getTokenExpiredDate(String token);

    Map<String,Object> register(String username,String password);

    Map<String,Object> login(String username, String password);
}
