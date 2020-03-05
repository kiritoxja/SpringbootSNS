package com.xja.springbootsns.controller;

import com.xja.springbootsns.model.*;
import com.xja.springbootsns.service.serviceInterface.CommentService;
import com.xja.springbootsns.service.serviceInterface.FollowService;
import com.xja.springbootsns.service.serviceInterface.QuestionService;
import com.xja.springbootsns.service.serviceInterface.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 *首页控制器
 **/

@Controller
public class IndexController {

    @Autowired
    QuestionService questionServiceImpl;

    @Autowired
    CommentService commentServiceImpl;

    @Autowired
    FollowService followServiceImpl;

    @Autowired
    UserService userServiceImpl;

    @Autowired
    LoginUser loginUser;

    private List<ViewObject> getQuestions(int userId, int offset, int limit){
        List<ViewObject> viewObjects = new ArrayList<>();
        List<Question> questionList = questionServiceImpl.getUserLatestQuestions(userId, offset, limit);
        for(Question question : questionList){
            ViewObject viewObject = new ViewObject();
            viewObject.put("question", question);
            viewObject.put("user", userServiceImpl.getUserById(question.getUserId()));
            viewObjects.add(viewObject);
        }
        return viewObjects;
    }
    @RequestMapping(value = {"/","/index","/index.html"})
    public String index(Model model){
        model.addAttribute("vos", getQuestions(0, 0, 10));
        return "index";
    }

    // 显示某个用户的详情页  发布的问题  关注和被关注列表
    @RequestMapping("/user/{userId}")
    public String userIndex(Model model, @PathVariable("userId")int userId){
        model.addAttribute("vos", getQuestions(userId, 0, 10));
        //显示关注和被关注列表
        User user = userServiceImpl.getUserById(userId);
        ViewObject vo = new ViewObject();
        vo.put("user", user);
        vo.put("commentCount", commentServiceImpl.getUserCommentCount(userId));
        vo.put("followeeCount", followServiceImpl.getFolloweeCount(userId, EntityType.ENTITY_USER));
        vo.put("followerCount", followServiceImpl.getFollowerCount(EntityType.ENTITY_USER, userId));
        if (loginUser.getUser() != null) {
            vo.put("followed", followServiceImpl.isFollower(loginUser.getUser().getId(), EntityType.ENTITY_USER, userId));
        } else {
            vo.put("followed", false);
        }
        model.addAttribute("profileUser", vo);
        return "profile";
    }
}
