package com.xja.springbootsns.dao;

import com.xja.springbootsns.model.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface CommentDao {

    String TABLE_NAME = " comment ";
    String INSERT_FIELDS=" content, user_id, created_date, entity_id, entity_type, status ";
    String INSERT_VALUES=" #{content}, #{userId}, #{createdDate}, #{entityId}, #{entityType}, #{status}";
    String SELECT_FIELDS=" id, "+INSERT_FIELDS;

    //添加一条评论
    @Insert(value = {"insert into ",TABLE_NAME,"(",INSERT_FIELDS,")"," values (",INSERT_VALUES,")"})
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    int addComment(Comment comment);

    //删除一条评论
    @Delete(value = {"delete from ",TABLE_NAME," where id=#{id}"})
    void deleteById(int id);

    //获得一条评论
    @Select(value = {"select ",SELECT_FIELDS, " from ",TABLE_NAME," where id=#{id}"})
    Comment selectById(int id);

    //更新一个实体中一条评论的状态
    @Update(value = {"update ", TABLE_NAME, " set status=#{status} where entity_type=#{entityType} and entity_id=#{entityId}"})
    void updateStatus(int entityType, int entityId, int status);

    //获得一个实体（如一个问题的所有评论,时间新的在前）
    @Select(value = {"select ", SELECT_FIELDS, " from ", TABLE_NAME,
            " where entity_type=#{entityType} and entity_id=#{entityId}"})
    List<Comment> selectByEntity(int entityType, int entityId);

    //获得一个实体的评论数
    @Select(value = {"select count(id) from ", TABLE_NAME,
            " where entity_type=#{entityType} and entity_id=#{entityId}"})
    int getEntityCommentCount(int entityType, int entityId);
}
