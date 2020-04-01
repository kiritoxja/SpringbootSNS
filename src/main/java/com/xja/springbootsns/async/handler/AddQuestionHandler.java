package com.xja.springbootsns.async.handler;

import com.xja.springbootsns.async.EventHandler;
import com.xja.springbootsns.async.EventModel;
import com.xja.springbootsns.async.EventType;
import com.xja.springbootsns.service.serviceInterface.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 *处理点赞事件 给用户发布站内信
 **/
@Component
public class AddQuestionHandler implements EventHandler {

    @Autowired
    SearchService searchService;

    @Override
    public void doHandle(EventModel model) {
        searchService.indexQuestion(model.getEntityId(),model.getExts("title"),model.getExts("cotent"));
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.ADD_QUESTION);
    }
}
