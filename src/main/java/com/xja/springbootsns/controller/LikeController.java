package com.xja.springbootsns.controller;

import com.xja.springbootsns.async.EventModel;
import com.xja.springbootsns.async.EventProducer;
import com.xja.springbootsns.async.EventType;
import com.xja.springbootsns.model.Comment;
import com.xja.springbootsns.model.EntityType;
import com.xja.springbootsns.model.LoginUser;
import com.xja.springbootsns.model.User;
import com.xja.springbootsns.service.serviceInterface.CommentService;
import com.xja.springbootsns.service.serviceInterface.LikeService;
import com.xja.springbootsns.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 接受点赞或踩的功能
 **/
@RestController
public class LikeController {

    @Autowired
    LoginUser loginUser;

    @Autowired
    LikeService likeService;

    @Autowired
    CommentService commentServiceImpl;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping("/like")
    public String like(int commentId, HttpServletRequest request) {
        User user = loginUser.getUser();
        if(null == user){
            return JsonUtil.getJsonString(999);
        }
        // 获取点赞的那条评论
        Comment comment = commentServiceImpl.getCommentById(commentId);
        // 异步队列发送私信给被赞人
        String url = request.getRequestURL().toString();
        StringBuilder link = new StringBuilder(url.substring(0, url.lastIndexOf('/')));
        link.append("/question/").append(comment.getEntityId());
        eventProducer.fireEvent(new EventModel(EventType.LIKE)
                .setActorId(loginUser.getUser().getId()).setEntityId(commentId)
                .setEntityType(EntityType.ENTITY_COMMENT).setEntityOwnerId(comment.getUserId())
                .setExts("link",link.toString()));
        //返回点赞数
        long likeCount = likeService.like(user.getId(), EntityType.ENTITY_COMMENT, commentId);
        return JsonUtil.getJsonString(String.valueOf(likeCount),0);
    }

    @RequestMapping("/dislike")
    public String dislike(int commentId) {
        User user = loginUser.getUser();
        if(null == user){
            return JsonUtil.getJsonString(999);
        }
        // 获取点赞的那条评论
        Comment comment = commentServiceImpl.getCommentById(commentId);
        //返回踩数
        long dislikeCount = likeService.dislike(user.getId(), EntityType.ENTITY_COMMENT, commentId);
        return JsonUtil.getJsonString(String.valueOf(dislikeCount),0);
    }
}
