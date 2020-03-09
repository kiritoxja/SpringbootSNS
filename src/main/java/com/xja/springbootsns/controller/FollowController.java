package com.xja.springbootsns.controller;

import com.xja.springbootsns.async.EventModel;
import com.xja.springbootsns.async.EventProducer;
import com.xja.springbootsns.async.EventType;
import com.xja.springbootsns.model.*;
import com.xja.springbootsns.service.serviceInterface.CommentService;
import com.xja.springbootsns.service.serviceInterface.FollowService;
import com.xja.springbootsns.service.serviceInterface.QuestionService;
import com.xja.springbootsns.service.serviceInterface.UserService;
import com.xja.springbootsns.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 **/
@Controller
public class FollowController {
    @Autowired
    FollowService followServiceImpl;
    @Autowired
    CommentService commentServiceImpl;
    @Autowired
    QuestionService questionServiceImpl;
    @Autowired
    UserService userServiceImpl;
    @Autowired
    LoginUser loginUser;
    @Autowired
    EventProducer eventProducer;

    @PostMapping(value = "/followUser")
    @ResponseBody
    public String followUser(int userId) { // userId是关注者的id
        if (loginUser.getUser() == null) {
            return JsonUtil.getJsonString(999);
        }
        boolean ret = followServiceImpl.follow(loginUser.getUser().getId(), EntityType.ENTITY_USER, userId);
        // 发送异步事件
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(loginUser.getUser().getId()).setEntityId(userId)
                .setEntityType(EntityType.ENTITY_USER).setEntityOwnerId(userId));
        // A关注了B  返回现在A关注的人数 但是我觉得应该是在B的详情页A关注了B  返回B现在的粉丝数用于回显
        //return JsonUtil.getJsonString(String.valueOf(followServiceImpl.getFolloweeCount(loginUser.getUser().getId(), EntityType.ENTITY_USER)),ret ? 0 : 1);
        return JsonUtil.getJsonString(String.valueOf(
                followServiceImpl.getFollowerCount(EntityType.ENTITY_USER, userId)),ret ? 0 : 1);

    }

    @PostMapping(value = "/unfollowUser")
    @ResponseBody
    public String unfollowUser(int userId) { // userId是取消关注者的id
        if (loginUser.getUser() == null) {
            return JsonUtil.getJsonString(999);
        }
        boolean ret = followServiceImpl.unfollow(loginUser.getUser().getId(), EntityType.ENTITY_USER, userId);
        // 取关发送异步事件
        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(loginUser.getUser().getId()).setEntityId(userId)
                .setEntityType(EntityType.ENTITY_USER).setEntityOwnerId(userId));
        // 返回粉丝人数
        return JsonUtil.getJsonString(String.valueOf(
                followServiceImpl.getFollowerCount(EntityType.ENTITY_USER, userId)),ret ? 0 : 1);
    }

    @PostMapping(value = "/followQuestion")
    @ResponseBody
    public String followQuestion(int questionId) {
        if (loginUser.getUser() == null) {
            return JsonUtil.getJsonString(999);
        }
        // 判断问题是否存在
        Question q = questionServiceImpl.getQuestionById(questionId);
        if (q == null) {
            return JsonUtil.getJsonString("问题不存在",1);
        }
        boolean ret = followServiceImpl.follow(loginUser.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);
        // 发送异步事件
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(loginUser.getUser().getId()).setEntityId(questionId)
                .setEntityType(EntityType.ENTITY_QUESTION).setEntityOwnerId(q.getUserId()));
        // 发送用户本人信息用于页面展示
        Map<String, Object> info = new HashMap<>();
        info.put("headUrl", loginUser.getUser().getHeadUrl());
        info.put("name", loginUser.getUser().getName());
        info.put("id", loginUser.getUser().getId());
        info.put("count", followServiceImpl.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));
        return JsonUtil.getJsonString(ret ? 0 : 1, info);
    }

    @PostMapping(value = "/unfollowQuestion")
    @ResponseBody
    public String unfollowQuestion(int questionId) {
        if (loginUser.getUser() == null) {
            return JsonUtil.getJsonString(999);
        }
        // 判断问题是否存在
        Question q = questionServiceImpl.getQuestionById(questionId);
        if (q == null) {
            return JsonUtil.getJsonString("问题不存在",1);
        }
        boolean ret = followServiceImpl.unfollow(loginUser.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);
        // 发送异步事件
        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(loginUser.getUser().getId()).setEntityId(questionId)
                .setEntityType(EntityType.ENTITY_QUESTION).setEntityOwnerId(q.getUserId()));
        // 发送用户本人信息用于页面展示
        Map<String, Object> info = new HashMap<>();
        info.put("id", loginUser.getUser().getId());
        info.put("count", followServiceImpl.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));
        return JsonUtil.getJsonString(ret ? 0 : 1, info);
    }

    @GetMapping(value = "/user/{uid}/followers")
    public String followers(Model model, @PathVariable("uid") int userId) {
        List<Integer> followerIds = followServiceImpl.getFollowers(EntityType.ENTITY_USER, userId, 0, 10);
        if (loginUser.getUser() != null) {
            model.addAttribute("followers", getUsersInfo(loginUser.getUser().getId(), followerIds));
        } else {
            model.addAttribute("followers", getUsersInfo(0, followerIds));
        }
        model.addAttribute("followerCount", followServiceImpl.getFollowerCount(EntityType.ENTITY_USER, userId));
        model.addAttribute("curUser", userServiceImpl.getUserById(userId));
        return "followers";
    }

    @GetMapping(value = "/user/{uid}/followees")
    public String followees(Model model, @PathVariable("uid") int userId) {
        List<Integer> followeeIds = followServiceImpl.getFollowees(userId, EntityType.ENTITY_USER, 0, 10);
        if (loginUser.getUser() != null) {
            model.addAttribute("followees", getUsersInfo(loginUser.getUser().getId(), followeeIds));
        } else {
            model.addAttribute("followees", getUsersInfo(0, followeeIds));
        }
        model.addAttribute("followeeCount", followServiceImpl.getFolloweeCount(userId, EntityType.ENTITY_USER));
        model.addAttribute("curUser", userServiceImpl.getUserById(userId));
        return "followees";
    }

    //获得用户信息 如果未登录则是未关注
    private List<ViewObject> getUsersInfo(int localUserId, List<Integer> userIds) {
        List<ViewObject> userInfos = new ArrayList<>();
        for (Integer uid : userIds) {
            User user = userServiceImpl.getUserById(uid);
            if (user == null) {
                continue;
            }
            ViewObject vo = new ViewObject();
            vo.put("user", user);
            vo.put("commentCount", commentServiceImpl.getUserCommentCount(uid));
            vo.put("followerCount", followServiceImpl.getFollowerCount(EntityType.ENTITY_USER, uid));
            vo.put("followeeCount", followServiceImpl.getFolloweeCount(uid, EntityType.ENTITY_USER));
            if (localUserId != 0) {
                vo.put("followed", followServiceImpl.isFollower(localUserId, EntityType.ENTITY_USER, uid));
            } else {
                vo.put("followed", false);
            }
            userInfos.add(vo);
        }
        return userInfos;
    }
}
