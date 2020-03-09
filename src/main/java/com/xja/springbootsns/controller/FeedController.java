package com.xja.springbootsns.controller;

import com.xja.springbootsns.model.EntityType;
import com.xja.springbootsns.model.Feed;
import com.xja.springbootsns.model.LoginUser;
import com.xja.springbootsns.model.User;
import com.xja.springbootsns.service.serviceInterface.FeedService;
import com.xja.springbootsns.service.serviceInterface.FollowService;
import com.xja.springbootsns.util.JedisAdapter;
import com.xja.springbootsns.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 *
 **/
@Controller
public class FeedController {

    private static  final Logger logger = LoggerFactory.getLogger(FeedController.class);

    @Autowired
    FeedService feedServiceImpl;
    @Autowired
    FollowService followServiceImpl;
    @Autowired
    LoginUser loginUser;
    @Autowired
    JedisAdapter jedisAdapter;


    @GetMapping(value = "/pushfeeds")
    private String getPushFeeds(Model model){
        try {
            //首先判断是否登录 如果没有则返回登录界面
            User user = loginUser.getUser();
            if(null == user){
                return "redirect:/reglogin?next=/pushfeeds";
            }
            //获取推模式下自己存储的所有新鲜事
            String timelineKey = RedisKeyUtil.getTimelineKey(user.getId());
            List<Feed> feeds = new LinkedList<>();
            //获取最新的10条新鲜事
            for(String id : jedisAdapter.zrevrange(timelineKey, 0, 9)){
                Optional<Feed> feed = Optional.ofNullable(feedServiceImpl.getById(Integer.valueOf(id)));
                feed.ifPresent(feed1 -> feeds.add(feed1));
            }
            model.addAttribute("feeds", feeds);
        } catch (Exception e) {
            logger.error("获取推模式新鲜事失败");
            e.printStackTrace();
        }
        return "feeds";
    }

    @GetMapping(value = "/pullfeeds")
    private String getPullFeeds(Model model) {
        try {
            //首先判断是否登录 如果没有则返回登录界面
            User user = loginUser.getUser();
            if(null == user){
                return "redirect:/reglogin?next=/pullfeeds";
            }
            //拉取所有关注者的新鲜事
            List<Integer> followees = followServiceImpl.getFollowees(user.getId(),
                    EntityType.ENTITY_USER, Integer.MAX_VALUE);
            if(null == followees || followees.size() == 0){
                //没有关注者不显示动态
                return "feeds";
            }
            List<Feed> feeds = feedServiceImpl.getUserFeeds(Integer.MAX_VALUE, followees, 10);
            model.addAttribute("feeds", feeds);
        } catch (Exception e) {
            logger.error("获取拉模式新鲜事失败");
            e.printStackTrace();
        }
        return "feeds";
    }
}
