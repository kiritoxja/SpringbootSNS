package com.xja.springbootsns.service.Impl;

import com.xja.springbootsns.dao.MessageDao;
import com.xja.springbootsns.model.Message;
import com.xja.springbootsns.service.serviceInterface.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 **/
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    MessageDao messageDao;

    @Override
    public int addMessage(Message message) {
        return messageDao.addMessage(message);
    }

    @Override
    public int getConversationUnreadMessageCount(int userId, String conversationId) {
        return messageDao.getConversationUnreadMessageCount(conversationId, userId);
    }

    @Override
    public List<Message> getConversationDetail(String conversationId, int offset, int limit) {
        return messageDao.getConversationDetail(conversationId, offset, limit);
    }

    @Override
    public List<Message> getConversationList(int userId, int offset, int limit) {
        return messageDao.getConversationListByUserId(userId, offset, limit);
    }

    @Override
    public void updateHasRead(String conversationId) {
        messageDao.updateHasRead(conversationId);
    }
}
