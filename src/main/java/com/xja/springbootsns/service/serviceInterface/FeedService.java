package com.xja.springbootsns.service.serviceInterface;

import com.xja.springbootsns.model.Feed;

import java.util.List;

public interface FeedService {
    // '拉'模式 拉取这些用户的新鲜事
    List<Feed> getUserFeeds(int maxId, List<Integer> userIds, int count);

    // '推'模式 将某个新鲜事的id推给一些用户  这些用户根据将列表中存储的新鲜事id进行查询
    Feed getById(int id);

    boolean addFeed(Feed feed);

    //得到某个用户的新鲜事
    List<Feed> getFeedsByUserId(int userId);
}
