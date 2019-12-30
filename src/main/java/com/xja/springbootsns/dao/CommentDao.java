package com.xja.springbootsns.dao;

import com.xja.springbootsns.model.Comment;
import org.apache.ibatis.annotations.*;

public interface CommentDao {

    String TABLE_NAME = " comment ";
    String INSERT_FIELDS=" content, user_id, created_date, entity_id, entity_type ";
    String INSERT_VALUES=" #{content}, #{user_id}, #{created_date}, #{entity_id}, #{entity_type}";
    String SELECT_FIELDS=" id, "+INSERT_FIELDS;

    @Insert(value = {"insert into ",TABLE_NAME,"(",INSERT_FIELDS,")"," values (",INSERT_VALUES,")"})
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    int addComment(Comment comment);

    @Delete(value = {"delete from ",TABLE_NAME," where id=#{id}"})
    void deleteById(int id);

    @Select(value = {"select ",SELECT_FIELDS, " from ",TABLE_NAME," where id=#{id}"})
    Comment selectById(int id);
}
