package com.xja.springbootsns.async.handler;

import com.alibaba.fastjson.JSONObject;
import com.xja.springbootsns.async.EventHandler;
import com.xja.springbootsns.async.EventModel;
import com.xja.springbootsns.async.EventType;
import com.xja.springbootsns.model.EntityType;
import com.xja.springbootsns.model.Feed;
import com.xja.springbootsns.model.Question;
import com.xja.springbootsns.model.User;
import com.xja.springbootsns.service.serviceInterface.FeedService;
import com.xja.springbootsns.service.serviceInterface.FollowService;
import com.xja.springbootsns.service.serviceInterface.QuestionService;
import com.xja.springbootsns.service.serviceInterface.UserService;
import com.xja.springbootsns.util.JedisAdapter;
import com.xja.springbootsns.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 异步处理 一个新鲜事产生之后的事件  目前支持评论 关注等新鲜事
 **/
@Component
public class FeedHandler implements EventHandler {

    @Autowired
    UserService userServiceImpl;

    @Autowired
    QuestionService questionServiceImpl;

    @Autowired
    FeedService feedServiceImpl;

    @Autowired
    FollowService followServiceImpl;

    @Autowired
    JedisAdapter jedisAdapter;

    //封装新鲜事数据  存储新鲜事信息
    private String buildFeedData(EventModel model){
        Map<String, String> map = new HashMap<>();
        // 往map里装触发用户信息
        User actor = userServiceImpl.getUserById(model.getActorId());
        if (actor == null) {
            return null;
        }
        map.put("userId", String.valueOf(actor.getId()));
        map.put("userHead", actor.getHeadUrl());
        map.put("userName", actor.getName());
        // 当评论一个问题或关注一个问题（不考虑关注人）的时候
        if (model.getType() == EventType.COMMENT || (model.getType() == EventType.FOLLOW && model.getEntityType() == EntityType.ENTITY_QUESTION)) {
            Question question = questionServiceImpl.getQuestionById(model.getEntityId());
            if (question == null) {
                return null;
            }
            // 往map里装问题信息
            map.put("questionId", String.valueOf(question.getId()));
            map.put("questionTitle", question.getTitle());
            return JSONObject.toJSONString(map);
        }
        return null;
    }

    @Override
    public void doHandle(EventModel model) {
        //推模式下 关注或取关用户要从自己的新鲜事列表中添加或删除该用户的所有新鲜事
        if(model.getType() == EventType.UNFOLLOW){
            if(model.getEntityType() == EntityType.ENTITY_USER){
                //取关用户事件 删除相应的新鲜事
                String timelineKey = RedisKeyUtil.getTimelineKey(model.getActorId());
                Set<String> feeds = jedisAdapter.zrevrange(timelineKey, 0, (int) jedisAdapter.zcard(timelineKey));
                int deleteUserId = model.getEntityOwnerId();
                //在这个list删除所有的userid为deleteuserid 的新鲜事
                List<Feed> deletefeeds = feedServiceImpl.getFeedsByUserId(deleteUserId);
                if(null != deletefeeds && deletefeeds.size() > 0){
                    for(Feed feed : deletefeeds){
                        if(feeds.contains(String.valueOf(feed.getId()))){
                            jedisAdapter.zrem(timelineKey, String.valueOf(feed.getId()));
                        }
                    }
                }
            }
        }else if(model.getType() == EventType.FOLLOW && model.getEntityType() == EntityType.ENTITY_USER){
            //关注用户 添加相应新鲜事
            String timelineKey = RedisKeyUtil.getTimelineKey(model.getActorId());
            int addUserId = model.getEntityOwnerId();
            //获得该用户的新鲜事
            List<Feed> addFeeds = feedServiceImpl.getFeedsByUserId(addUserId);
            for(Feed feed : addFeeds){
                jedisAdapter.zadd(timelineKey, feed.getCreatedDate().getTime(), String.valueOf(feed.getId()));
            }
        }else{
            //评论或关注问题事件
            // 构造一个新鲜事  注意在数据库中添加该新鲜事后注意返回主键 否则pull的时候存的主键不对
            Feed feed = new Feed();
            feed.setCreatedDate(new Date());
            feed.setType(model.getType().getValue());
            feed.setUserId(model.getActorId());
            feed.setData(buildFeedData(model));
            if (feed.getData() == null) {
                // 不支持的feed
                return;
            }
            //拉取的话只需要添加到数据库即可  推的话要添加到每个粉丝的存储列表
            feedServiceImpl.addFeed(feed);
            // 获得所有粉丝
            List<Integer> followers = followServiceImpl.getFollowers(EntityType.ENTITY_USER, model.getActorId(), Integer.MAX_VALUE);
            // 给未登录用户也推送
            //followers.add(0);
            // 给所有粉丝推事件 存储feed的id
            for (int follower : followers) {
                String timelineKey = RedisKeyUtil.getTimelineKey(follower);
                jedisAdapter.zadd(timelineKey,feed.getCreatedDate().getTime(), String.valueOf(feed.getId()));
            }
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(new EventType[]{EventType.COMMENT, EventType.FOLLOW, EventType.UNFOLLOW});
    }
}
