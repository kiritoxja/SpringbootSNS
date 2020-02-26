package com.xja.springbootsns.async;

import com.alibaba.fastjson.JSON;
import com.xja.springbootsns.util.JedisAdapter;
import com.xja.springbootsns.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 *消费者  取出事件交给handler处理
 **/

@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {

    @Autowired
    JedisAdapter jedisAdapter;

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    // 通过map实现的最简单的消息分发 将一个类型的事件交由负责这个类型事件的handlers 处理
    private Map<EventType, List<EventHandler>> config = new HashMap<>();
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        //初始化哪些handler处理哪些类型的事件
        Optional<Map<String,EventHandler>> handlers =  Optional.ofNullable(applicationContext.getBeansOfType(EventHandler.class));
        handlers.ifPresent( handlerMap->{
            for (String key : handlerMap.keySet()) {
                EventHandler eventHandler = handlerMap.get(key);
                for (EventType eventType : eventHandler.getSupportEventTypes()) {
                    if(! config.containsKey(eventType)){
                        config.put(eventType, new ArrayList<>());
                    }
                    config.get(eventType).add(eventHandler);
                }
            }
        });

        //开启线程池分发事件队列里的事件
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    //获取事件
                    String key = RedisKeyUtil.getEventQueueKey();
                    // 获取list最右边的值（先进先出），如果没有值会一直阻塞知道有值可弹出
                    List<String> events = jedisAdapter.brpop(0, key);
                    for (String message : events) {
                        // 前面brpop的返回值第一个值是key，应该过滤掉
                        if (message.equals(key)) {
                            continue;
                        }
                        // 解析message，找到EventModel
                        EventModel eventModel = JSON.parseObject(message, EventModel.class);
                        if (!config.containsKey(eventModel.getType())) {
                            System.out.println(eventModel.getType());
                            logger.error("不能识别的事件");
                            continue;
                        }
                        // 找到handler list，一个个去处理这个event
                        for (EventHandler handler : config.get(eventModel.getType())) {
                            handler.doHandle(eventModel);
                        }
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
