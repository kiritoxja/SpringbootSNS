package com.xja.springbootsns.service.Impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import com.xja.springbootsns.dao.UserDao;
import com.xja.springbootsns.exception.ExpiredJwtException;
import com.xja.springbootsns.model.User;
import com.xja.springbootsns.service.serviceInterface.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 *
 **/
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

    @Override
    public User getUserById(int id) {
        return userDao.selectById(id);
    }

    @Override
    public User getUserByName(String name) {
        return userDao.selectByName(name);
    }

    @Override
    public String getToken(User user) {
        Map<String,Object> headerClaims = new HashMap<>();
        headerClaims.put("alg", tokenAlgorithm);
        headerClaims.put("type", tokenType);
        String token = JWT.create().withHeader(headerClaims)
                .withAudience(String.valueOf(user.getId()))
                .withExpiresAt(new Date(System.currentTimeMillis()+tokenExpiredTime))
                .sign(Algorithm.HMAC256(user.getPassword()));
        return token ;
    }

    @Override
    public int verifyToken(String token){
        int verifyResult = -1;
        if(! StringUtils.isEmpty(token)){
            DecodedJWT decode= JWT.decode(token);
            int userId = Integer.valueOf(decode.getAudience().get(0));
            User user = userDao.selectById(userId);
            if( user != null){
                JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
                try {
                    jwtVerifier.verify(token);
                    verifyResult = userId;
                } catch (JWTVerificationException e){
                }
            }
        }
        return verifyResult;
    }

    @Override
    public Date getTokenExpiredDate(String token) {
        if(! StringUtils.isEmpty(token)) {
            DecodedJWT decode = JWT.decode(token);
            return decode.getExpiresAt();
        }
        return null;
    }

    @Override
    public Map<String, Object> register(String username, String password) {
        Map<String, Object> resultMap = new HashMap<>();
        User user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));
        String savedPassword = DigestUtils.md5DigestAsHex((password + user.getSalt()).getBytes());
        user.setPassword(savedPassword);
        String head = String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000));
        user.setHeadUrl(head);
        userDao.addUser(user);
        resultMap.put("token", getToken(user));
        return resultMap;
    }

    @Override
    public Map<String, Object> login(String username, String password) {
        Map<String, Object> result = new HashMap<>();
        if (StringUtils.isEmpty(username)) {
            result.put("msg", "用户名不能为空");
            return result;
        }

        User user = getUserByName(username);
        if (user == null) {
            result.put("msg", "用户名不存在");
            return result;
        }

        if (!DigestUtils.md5DigestAsHex((password + user.getSalt()).getBytes()).equals(user.getPassword())) {
            result.put("msg", "密码错误");
            return result;
        }

        String token = getToken(user);
        result.put("token", token);
        return result;
    }

}
