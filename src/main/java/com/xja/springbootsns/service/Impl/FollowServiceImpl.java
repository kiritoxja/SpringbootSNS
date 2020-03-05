package com.xja.springbootsns.service.Impl;

import com.xja.springbootsns.service.serviceInterface.FollowService;
import com.xja.springbootsns.util.JedisAdapter;
import com.xja.springbootsns.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.*;

/**
 *
 **/
@Service
public class FollowServiceImpl implements FollowService {
    @Autowired
    JedisAdapter jedisAdapter;

    @Override
    public boolean follow(int userId, int entityType, int entityId) {
        //存储某个实体的follower
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        //获取某个用户关注的followee
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        //事务操作 同时成功或失败
        Date date = new Date();
        // 粉丝+1
        tx.zadd(followerKey,date.getTime(),String.valueOf(userId));
        // 关注者+1
        tx.zadd(followeeKey, date.getTime(), String.valueOf(entityId));
        List<Object> ret = jedisAdapter.exec(tx, jedis);
        return ret.size() == 2 && (Long) ret.get(0) > 0 && (Long) ret.get(1) > 0;
    }

    @Override
    public boolean unfollow(int userId, int entityType, int entityId) {
        //存储某个实体的follower
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        //获取某个用户关注的followee
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        //事务操作 同时成功或失败
        // 粉丝-1
        tx.zrem(followerKey,String.valueOf(userId));
        // 关注者-1
        tx.zrem(followeeKey,String.valueOf(entityId));
        List<Object> ret = jedisAdapter.exec(tx, jedis);
        return ret.size() == 2 && (Long) ret.get(0) > 0 && (Long) ret.get(1) > 0;
    }

    @Override
    public List<Integer> getFollowers(int entityType, int entityId, int count) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return idsFormat(jedisAdapter.zrevrange(followerKey, 0, count-1));
    }

    @Override
    public List<Integer> getFollowers(int entityType, int entityId, int offset, int count) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return idsFormat(jedisAdapter.zrevrange(followerKey, offset, offset + count - 1));
    }

    @Override
    public List<Integer> getFollowees(int userId, int entityType, int count) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return idsFormat(jedisAdapter.zrevrange(followeeKey, 0, count-1));
    }

    @Override
    public List<Integer> getFollowees(int userId, int entityType, int offset, int count) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return idsFormat(jedisAdapter.zrevrange(followeeKey, offset, offset + count - 1));
    }

    @Override
    public long getFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zcard(followerKey);
    }

    @Override
    public long getFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return jedisAdapter.zcard(followeeKey);
    }

    //将字符串id集合转化为 整数集合
    public List<Integer> idsFormat (Set<String> ids){
        List res = new ArrayList();
        for(String s : ids){
            res.add(Integer.valueOf(s));
        }
        return res;
    }

    @Override
    //用户是否关注了某个实体
    public boolean isFollower(int userId, int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zscore(followerKey,String.valueOf(userId)) != null;
    }
}
