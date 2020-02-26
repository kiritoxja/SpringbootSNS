package com.xja.springbootsns.controller;

import com.xja.springbootsns.model.Comment;
import com.xja.springbootsns.model.EntityType;
import com.xja.springbootsns.model.LoginUser;
import com.xja.springbootsns.model.User;
import com.xja.springbootsns.service.Impl.QuestionServiceImpl;
import com.xja.springbootsns.service.serviceInterface.CommentService;
import com.xja.springbootsns.service.serviceInterface.QuestionService;
import com.xja.springbootsns.service.serviceInterface.SensitiveService;
import com.xja.springbootsns.service.serviceInterface.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;


/**
 *
 **/
@Controller
public class CommentController {

    private static  final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    LoginUser loginUser;

    @Autowired
    UserService userServiceImp;

    @Autowired
    CommentService commentServiceImpl;

    @Autowired
    QuestionService questionServiceImpl;

    @Autowired
    SensitiveService sensitiveServiceImpl;

    //添加一条评论
    @PostMapping("/comment/addComment")
    String addComment(int questionId, String content){
        try{
            //首先判断是否登录 如果没有则返回登录界面
            User user = loginUser.getUser();
            if(null == user){
                return "redirect:/reglogin?next=/question/"+questionId;
            }
            content = HtmlUtils.htmlEscape(content);
            content = sensitiveServiceImpl.filter(content);
            Comment comment = new Comment(content, user.getId(), new Date(),
                    questionId, EntityType.ENTITY_QUESTION, 0);
            int a = 2/0 ;
            //插入评论和问题的评论数增加应该是一个事务  以后异步实现
            commentServiceImpl.addComment(comment);
            int count = commentServiceImpl.getCommentCount(questionId, EntityType.ENTITY_QUESTION);
            questionServiceImpl.updateCommentCount(questionId,count);
          } catch (Exception e) {
            logger.error("添加评论失败：" + e.getMessage());
        }
        return "redirect:/question/" + questionId;
    }
}
