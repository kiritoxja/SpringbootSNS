package com.xja.springbootsns.service.Impl;

import com.xja.springbootsns.dao.FeedDao;
import com.xja.springbootsns.model.Feed;
import com.xja.springbootsns.service.serviceInterface.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 **/
@Service
public class FeedServiceImpl implements FeedService {

    @Autowired
    FeedDao feedDao;

    @Override
    public List<Feed> getUserFeeds(int maxId, List<Integer> userIds, int count) {
        return feedDao.selectUserFeeds(maxId, userIds, count);
    }

    @Override
    public Feed getById(int id) {
        return feedDao.getFeedById(id);
    }

    @Override
    public boolean addFeed(Feed feed) {
        return feedDao.addFeed(feed) > 0 ;
    }

    @Override
    public List<Feed> getFeedsByUserId(int userId) {
        return feedDao.selectFeedsByUserId(userId);
    }
}
