package com.xja.springbootsns.service.serviceInterface;

import com.xja.springbootsns.model.Question;

import java.util.List;

public interface QuestionService {
    //获得用户最近发布的问题 如果userId为0 则是获取所有用户最近发布的问题
    List<Question> getUserLatestQuestions(int userId , int offset, int limit);

    int addQuestion(Question question);

    int addQuestion(String content, String title);

    Question getQuestionById(int id);

    void updateCommentCount(int id, int count);
}
