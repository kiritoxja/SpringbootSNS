package com.xja.springbootsns.service.Impl;

import com.xja.springbootsns.service.serviceInterface.LikeService;
import com.xja.springbootsns.util.JedisAdapter;
import com.xja.springbootsns.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 点赞和踩的服务
 **/
@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    JedisAdapter jedisAdapter;

    @Override
    //返回点赞人数 - 点踩人数
    public long getLikeCount(int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        String dislikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        return jedisAdapter.scard(likeKey) - jedisAdapter.scard(dislikeKey) ;
    }

    @Override
    // 获取当前user喜欢或不喜欢状态 1 喜欢  0 没有 -1踩
    public int getLikeStatus(int userId, int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        if (jedisAdapter.sismember(likeKey, String.valueOf(userId))) {
            return 1;
        }
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        return jedisAdapter.sismember(disLikeKey, String.valueOf(userId)) ? -1 : 0;
    }

    @Override
    public long like(int userId, int entityType, int entityId) {
        // 从喜欢的集合里添加这个userId
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        jedisAdapter.sadd(likeKey, String.valueOf(userId));
        // 从不喜欢的集合里删除这个userId
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        jedisAdapter.srem(disLikeKey, String.valueOf(userId));
        // 返回喜欢人数
        return getLikeCount(entityType, entityId);
    }

    @Override
    public long dislike(int userId, int entityType, int entityId) {
        // 从不喜欢的集合里添加这个userId
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        jedisAdapter.sadd(disLikeKey, String.valueOf(userId));
        // 从喜欢的集合里删除这个userId
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        jedisAdapter.srem(likeKey, String.valueOf(userId));
        // 返回喜欢人数
        return getLikeCount(entityType, entityId);
    }
}
