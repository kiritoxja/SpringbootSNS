package com.xja.springbootsns.controller;

import com.xja.springbootsns.model.LoginUser;
import com.xja.springbootsns.model.Message;
import com.xja.springbootsns.model.User;
import com.xja.springbootsns.model.ViewObject;
import com.xja.springbootsns.service.serviceInterface.MessageService;
import com.xja.springbootsns.service.serviceInterface.SensitiveService;
import com.xja.springbootsns.service.serviceInterface.UserService;
import com.xja.springbootsns.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 **/
@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    private int unLoginCode = 999;

    @Autowired
    LoginUser loginUser;

    @Autowired
    MessageService messageServiceImpl;

    @Autowired
    UserService userServiceImpl;

    @Autowired
    SensitiveService sensitiveServiceImpl;

    @PostMapping("/msg/addMessage")
    @ResponseBody
    String addMessage(String toName, String content){
        try {
            User fromUser = loginUser.getUser();
            //验证是否登录
            if(null == fromUser){
                return JsonUtil.getJsonString("未登录用户添加私信",unLoginCode);
            }
            //获得私信发给谁
            User toUser = userServiceImpl.getUserByName(toName);
            if(null == toUser){
                return JsonUtil.getJsonString("私信用户不存在", 1);
            }
            content = HtmlUtils.htmlEscape(content);
            content = sensitiveServiceImpl.filter(content);
            Message message = new Message(fromUser.getId(),toUser.getId(),content,new Date());
            if(messageServiceImpl.addMessage(message) > 0){
                return JsonUtil.getJsonString(0);
            }
        } catch (Exception e) {
            logger.error("添加私信出错："+ e.getMessage());
        }
        return JsonUtil.getJsonString("添加私信失败",1);
    }

    @RequestMapping("/msg/list")
    String getConversationList(Model model){
        try {
            User localUser = loginUser.getUser();
            int userId = loginUser.getUser().getId();
            //可以做分页功能
            List<Message> messageList = messageServiceImpl.getConversationList(userId, 0, 10);
            List<ViewObject> messages = new ArrayList<>();
            for(Message message : messageList){
                ViewObject viewObject = new ViewObject();
                message.setId(message.getId());
                viewObject.put("conversation",message);
                //添加另一方user的信息
                int anotherUserId = message.getFromId() == localUser.getId() ? message.getToId() : message.getFromId();
                viewObject.put("user",userServiceImpl.getUserById(anotherUserId));
                viewObject.put("unread",
                        messageServiceImpl.getConversationUnreadMessageCount(userId, message.generateConversationId()));
                messages.add(viewObject);
            }
            model.addAttribute("conversations", messages);
        } catch (Exception e) {
            logger.error("获取用户私信列表失败："+e.getMessage());
        }
        return "letter";
    }

    //查看了某会话的所有私信 unread 全部置为1
    @RequestMapping("/msg/detail")
    String getConversationDetai(Model model, String conversationId){
        try {
            List<Message> messageList = messageServiceImpl.getConversationDetail(conversationId, 0, 10);
            List<ViewObject> messages = new ArrayList<>();
            for(Message message: messageList){
                ViewObject viewObject = new ViewObject();
                //添加发送私信者的头像
                User user = userServiceImpl.getUserById(message.getFromId());
                viewObject.put("headUrl",user.getHeadUrl());
                viewObject.put("message",message);
                viewObject.put("userName",user.getName());
                //将状态改为已读
                messageServiceImpl.updateHasRead(message.getConversationId());
                messages.add(viewObject);
            }
            model.addAttribute("messages", messages);
        } catch (Exception e) {
            logger.error("查看会话全部私信失败："+e.getMessage());
        }
        return "letterDetail";
    }
}
