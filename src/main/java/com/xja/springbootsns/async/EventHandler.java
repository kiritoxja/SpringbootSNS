package com.xja.springbootsns.async;

import java.util.List;

public interface EventHandler {
    // 处理event的方法
    void doHandle(EventModel model);
    // Handler可以处理哪些事件
    List<EventType> getSupportEventTypes();
}
