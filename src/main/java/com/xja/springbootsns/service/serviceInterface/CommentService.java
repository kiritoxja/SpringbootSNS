package com.xja.springbootsns.service.serviceInterface;

import com.xja.springbootsns.model.Comment;

import java.util.List;

public interface CommentService {

    List<Comment> getCommentsByEntity(int entityId, int entityType);

    int addComment(Comment comment);

    int getCommentCount(int entityId, int entityType);

    void deleteComment(int entityId, int entityType);

    Comment getCommentById(int commentId);
}
