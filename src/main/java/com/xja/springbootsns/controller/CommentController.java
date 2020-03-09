package com.xja.springbootsns.controller;

import com.xja.springbootsns.async.EventModel;
import com.xja.springbootsns.async.EventProducer;
import com.xja.springbootsns.async.EventType;
import com.xja.springbootsns.model.Comment;
import com.xja.springbootsns.model.EntityType;
import com.xja.springbootsns.model.LoginUser;
import com.xja.springbootsns.model.User;
import com.xja.springbootsns.service.serviceInterface.CommentService;
import com.xja.springbootsns.service.serviceInterface.QuestionService;
import com.xja.springbootsns.service.serviceInterface.SensitiveService;
import com.xja.springbootsns.service.serviceInterface.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
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

    @Autowired
    EventProducer eventProducer;

    //添加一条评论
    @PostMapping("/comment/addComment")
    @Transactional(rollbackFor = Exception.class)
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
            //插入评论和问题的评论数增加应该是一个事务
            commentServiceImpl.addComment(comment);
            int count = commentServiceImpl.getCommentCount(questionId, EntityType.ENTITY_QUESTION);
            questionServiceImpl.updateCommentCount(questionId,count);
            // 推送异步事件 推送新鲜事
            eventProducer.fireEvent(new EventModel(EventType.COMMENT).
                    setActorId(comment.getUserId()).setEntityId(questionId).setEntityType(EntityType.ENTITY_QUESTION));
          } catch (Exception e) {
            logger.error("添加评论失败：" + e.getMessage());
            //进行回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return "redirect:/question/" + questionId;
    }
}
