package com.xja.springbootsns.service.serviceInterface;

import com.xja.springbootsns.model.Message;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 **/
public interface MessageService {

    int addMessage(Message message);

    int getConversationUnreadMessageCount(int userId, String conversationId);

    List<Message> getConversationDetail(String conversationId, int offset, int limit);

    List<Message> getConversationList(int userId, int offset, int limit);

    void updateHasRead(String conversationId);
}
