package com.xja.springbootsns.service.Impl;

import com.xja.springbootsns.dao.QuestionDao;
import com.xja.springbootsns.model.LoginUser;
import com.xja.springbootsns.model.Question;
import com.xja.springbootsns.model.User;
import com.xja.springbootsns.service.serviceInterface.QuestionService;
import com.xja.springbootsns.service.serviceInterface.SensitiveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;
import java.util.List;

/**
 *
 **/
@Service
public class QuestionServiceImpl implements QuestionService {

    private static final Logger logger = LoggerFactory.getLogger(QuestionServiceImpl.class);
    //未登录用户发布问题返回code
    private int unLoginCode = 999;

    @Autowired
    QuestionDao questionDao;

    @Autowired
    LoginUser loginUser;

    @Autowired
    SensitiveService sensitiveServiceImpl;

    @Override
    public List<Question> getUserLatestQuestions(int userId, int offset, int limit) {
        return questionDao.selectLatestQuestions(userId, offset, limit);
    }

    @Override
    public int addQuestion(Question question) {
        String content = question.getContent();
        String title = question.getTitle();
        //xss过滤
        content = HtmlUtils.htmlEscape(content);
        title = HtmlUtils.htmlEscape(title);
        //敏感词过滤
        content = sensitiveServiceImpl.filter(content);
        title = sensitiveServiceImpl.filter(title);
        question.setContent(content);
        question.setTitle(title);
        return questionDao.addQuestion(question);
    }

    @Override
    public int addQuestion(String content, String title) {
        try{
            //xss过滤
            content = HtmlUtils.htmlEscape(content);
            title = HtmlUtils.htmlEscape(title);
            //敏感词过滤
            content = sensitiveServiceImpl.filter(content);
            title = sensitiveServiceImpl.filter(title);
            User user = loginUser.getUser();
            Question question = new Question();
            question.setUserId(user.getId());
            question.setContent(content);
            question.setCreatedDate(new Date());
            question.setTitle(title);
            return questionDao.addQuestion(question);
        } catch (Exception e) {
            logger.error("发布问题出错："+ e.getMessage());
        }
        return 0;
    }

    @Override
    public Question getQuestionById(int id) {
        return questionDao.selectQuestionById(id);
    }

    @Override
    public void updateCommentCount(int id, int count) {
        questionDao.updateCommentCount(count, id);
    }
}
