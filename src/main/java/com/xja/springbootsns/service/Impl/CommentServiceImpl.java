package com.xja.springbootsns.service.Impl;

import com.xja.springbootsns.dao.CommentDao;
import com.xja.springbootsns.model.Comment;
import com.xja.springbootsns.service.serviceInterface.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 **/
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    CommentDao commentDao;

    @Override
    public List<Comment> getCommentsByEntity(int entityId, int entityType) {
        return commentDao.selectByEntity(entityType, entityId);
    }

    @Override
    public int addComment(Comment comment) {
        return commentDao.addComment(comment);
    }

    @Override
    public int getCommentCount(int entityId, int entityType) {
        return commentDao.getEntityCommentCount(entityType, entityId);
    }

    @Override
    public void deleteComment(int entityId, int entityType) {
        commentDao.updateStatus(entityType, entityId, 1);
    }

    @Override
    public Comment getCommentById(int commentId) {
        return commentDao.selectById(commentId);
    }
}
