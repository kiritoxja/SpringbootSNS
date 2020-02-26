package com.xja.springbootsns.async;

import com.alibaba.fastjson.JSONObject;
import com.xja.springbootsns.util.JedisAdapter;
import com.xja.springbootsns.util.JsonUtil;
import com.xja.springbootsns.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 事件处理类  生产者存入缓存队列
 **/
@Service
public class EventProducer {
    @Autowired
    JedisAdapter jedisAdapter;

    //将事件存入缓存队列
    public boolean fireEvent(EventModel eventModel){
        try {
            String event = JSONObject.toJSONString(eventModel);
            jedisAdapter.lpush(RedisKeyUtil.getEventQueueKey(), event);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
