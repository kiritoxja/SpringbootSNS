package com.xja.springbootsns.service.serviceInterface;

import com.xja.springbootsns.model.Question;

import java.util.List;

public interface QuestionService {
    List<Question> getUserLatestQuestions(int userId , int offset, int limit);
}
