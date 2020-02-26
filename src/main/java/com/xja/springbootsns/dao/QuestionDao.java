package com.xja.springbootsns.dao;

import com.xja.springbootsns.model.Question;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface QuestionDao {

    String TABLE_NAME = " question ";
    String INSERT_FIELDS=" title, content, user_id, created_date, comment_count ";
    String INSERT_VALUES=" #{title}, #{content}, #{userId}, #{createdDate}, #{commentCount}";
    String SELECT_FIELDS=" id, "+INSERT_FIELDS;

    @Insert(value = {"insert into ",TABLE_NAME,"(",INSERT_FIELDS,")"," values (",INSERT_VALUES,")"})
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    int addQuestion(Question question);

    List<Question> selectLatestQuestions(@Param("userId") int userId, @Param("offset") int offset,
                                         @Param("limit") int limit);

    @Select(value = {"select", SELECT_FIELDS, " from ", TABLE_NAME, "where id = #{id}"})
    Question selectQuestionById(int id);

    @Update(value = {"update ", TABLE_NAME, " set comment_count=#{commentCount} where id=#{id}"})
    void updateCommentCount(int commentCount, int id);
}
