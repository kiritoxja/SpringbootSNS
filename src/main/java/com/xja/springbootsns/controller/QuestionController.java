package com.xja.springbootsns.controller;

import com.xja.springbootsns.model.*;
import com.xja.springbootsns.service.serviceInterface.*;
import com.xja.springbootsns.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 *
 **/
@Controller
public class QuestionController {

    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);
    //未登录用户发布问题返回code
    private int unLoginCode = 999;

    @Autowired
    QuestionService questionServiceImpl;

    @Autowired
    UserService userServiceImpl;

    @Autowired
    LoginUser loginUser;

    @Autowired
    CommentService commentServiceImpl;

    @Autowired
    LikeService likeServiceImpl;

    @Autowired
    FollowService followServiceImpl;

    //发布一个问题
    @PostMapping("/question/add")
    @ResponseBody
    public String addQuestion(@RequestParam("title") String title,@RequestParam("content") String content){
        try {
            //验证是否登录
            if(null == loginUser.getUser()){
                return JsonUtil.getJsonString("未登录用户发布问题",unLoginCode);
            }
            if(questionServiceImpl.addQuestion(content,title) > 0){
                return JsonUtil.getJsonString(0);
            }
        } catch (Exception e) {
            logger.error("发布问题出错："+ e.getMessage());
        }
        return JsonUtil.getJsonString("发布问题失败",1);
    }

    //展示问题细节
    @RequestMapping("/question/{qid}")
    public String showQuestioinDetail(Model model, @PathVariable("qid") int qid){
        Question question = questionServiceImpl.getQuestionById(qid);
        User user = userServiceImpl.getUserById(question.getUserId());
        User logUser = loginUser.getUser();
        model.addAttribute("question", question);
        model.addAttribute("user", user);
        //添加评论数据
        List<ViewObject> comments = new ArrayList<>();
        List<Comment> commentList = commentServiceImpl.getCommentsByEntity(qid, EntityType.ENTITY_QUESTION);
        for(Comment comment : commentList){
            ViewObject viewObject = new ViewObject();
            viewObject.put("user", userServiceImpl.getUserById(comment.getUserId()));
            viewObject.put("comment", comment);
            //获取评论点赞信息
            int liked;
            if(null == logUser){
                liked = 0;
            }else{
                liked = likeServiceImpl.getLikeStatus(logUser.getId(), EntityType.ENTITY_COMMENT,comment.getId());
            }
            viewObject.put("liked", liked);
            viewObject.put("likeCount", likeServiceImpl.getLikeCount(EntityType.ENTITY_COMMENT, comment.getId()));
            comments.add(viewObject);
        }
        model.addAttribute("comments", comments);
        // 获取关注的用户信息
        List<ViewObject> followUsers = new ArrayList<>();
        List<Integer> users = followServiceImpl.getFollowers(EntityType.ENTITY_QUESTION, qid, 20);
        for (Integer userId : users) {
            ViewObject vo = new ViewObject();
            User u = userServiceImpl.getUserById(userId);
            if (u == null) {
                continue;
            }
            vo.put("name", u.getName());
            vo.put("headUrl", u.getHeadUrl());
            vo.put("id", u.getId());
            followUsers.add(vo);
        }
        model.addAttribute("followUsers", followUsers);
        if (loginUser.getUser() != null) {
            model.addAttribute("followed", followServiceImpl.isFollower(loginUser.getUser().getId(), EntityType.ENTITY_QUESTION, qid));
        } else {
            model.addAttribute("followed", false);
        }
        return "questionDetail";
    }
}
