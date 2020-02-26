package com.xja.springbootsns.service.serviceInterface;

public interface LikeService {
    long getLikeCount(int entityType, int entityId);
    int getLikeStatus(int userId, int entityType, int entityId);
    long like(int userId, int entityType, int entityId);
    long dislike(int userId, int entityType, int entityId);
}
