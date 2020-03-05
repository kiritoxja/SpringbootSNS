package com.xja.springbootsns.service.serviceInterface;

import java.util.List;

public interface FollowService {
    // 用户关注了某个实体,可以关注问题,关注用户,关注评论等任何实体
    boolean follow(int userId, int entityType, int entityId);

    // 取消关注
    boolean unfollow(int userId, int entityType, int entityId);

    //获取某个类型的某个实体的前多少个关注者
    List<Integer> getFollowers(int entityType, int entityId, int count);

    List<Integer> getFollowers(int entityType, int entityId, int offset, int count);

    //获取用户关注了哪些问题  评论等
    List<Integer> getFollowees(int userId, int entityType, int count);

    List<Integer> getFollowees(int userId, int entityType, int offset, int count);

    //返回这个问题或人的关注者有多少
    long getFollowerCount(int entityType, int entityId);

    //获取这个用户关注了多少人，问题等
    long getFolloweeCount(int userId, int entityType);

    //判断某用户是否关注了某个实体
    boolean isFollower(int userId, int entityType, int entityId);

}
