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
 * 处理关注事件 发送站内信
 **/
@Component
public class FollowHandler implements EventHandler {
    @Autowired
    MessageService messageServiceImpl;
    @Autowired
    UserService userServiceImpl;

    @Override
    public void doHandle(EventModel model) {
        // 给被关注者发私信
        int fromId= ForumUtil.SYSTEMCONTROLLER_USERID;
        int toId = model.getEntityOwnerId();
        Message message = new Message();
        message.setFromId(fromId);
        message.setToId(toId);
        message.setCreatedDate(new Date());
        message.setConversationId(message.generateConversationId());
        User user = userServiceImpl.getUserById(model.getActorId());
        if (model.getEntityType() == EntityType.ENTITY_QUESTION) {
            message.setContent("用户'" + user.getName() + "'关注了你的问题,http://127.0.0.1:8080/question/" + model.getEntityId());
        } else if (model.getEntityType() == EntityType.ENTITY_USER) {
            message.setContent("用户'" + user.getName() + "'关注了你,http://127.0.0.1:8080/user/" + model.getActorId());
        }
        messageServiceImpl.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.FOLLOW);
    }
}
