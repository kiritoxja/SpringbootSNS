package com.xja.springbootsns.async.handler;

import com.xja.springbootsns.async.EventHandler;
import com.xja.springbootsns.async.EventModel;
import com.xja.springbootsns.async.EventType;
import com.xja.springbootsns.model.EntityType;
import com.xja.springbootsns.model.Message;
import com.xja.springbootsns.model.User;
import com.xja.springbootsns.service.serviceInterface.MessageService;
import com.xja.springbootsns.service.serviceInterface.UserService;
import com.xja.springbootsns.util.ForumUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 *处理点赞事件 给用户发布站内信
 **/
@Component
public class LikeHandler implements EventHandler {
    @Autowired
    MessageService messageServiceImpl;
    @Autowired
    UserService userServiceImpl;


    @Override
    public void doHandle(EventModel model) {
        // 给被赞的人发message
        int fromId = ForumUtil.SYSTEMCONTROLLER_USERID;
        int toId = model.getEntityOwnerId();
        Message message = new Message();
        message.setFromId(fromId);
        message.setToId(toId);
        message.setCreatedDate(new Date());
        message.setConversationId(message.generateConversationId());
        User user = userServiceImpl.getUserById(model.getActorId());
        message.setContent("用户'" + user.getName() + "'赞了你的"+ EntityType.getEntityTypeName(model.getEntityType())
                + model.getExts("link"));
        messageServiceImpl.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE); // 只关注LIKE的事件
    }
}
