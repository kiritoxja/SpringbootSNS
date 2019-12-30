package com.xja.springbootsns.service.Impl;

import com.xja.springbootsns.dao.QuestionDao;
import com.xja.springbootsns.model.Question;
import com.xja.springbootsns.service.serviceInterface.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 **/
@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    QuestionDao questionDao;

    @Override
    public List<Question> getUserLatestQuestions(int userId, int offset, int limit) {
        return questionDao.selectLatestQuestions(userId, offset, limit);
    }
}
