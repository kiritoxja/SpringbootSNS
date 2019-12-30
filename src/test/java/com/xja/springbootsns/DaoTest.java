package com.xja.springbootsns;

import com.xja.springbootsns.dao.QuestionDao;
import com.xja.springbootsns.dao.UserDao;
import com.xja.springbootsns.model.Question;
import com.xja.springbootsns.model.User;
import com.xja.springbootsns.model.ViewObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Date;
import java.util.Random;

/**
 *
 **/
@SpringBootTest
@Sql("/createTable.sql")
public class DaoTest {

    @Autowired
    UserDao userDao;

    @Autowired
    QuestionDao questionDao;

    @Test
    void testUserDaoInsert(){
        User user = new User();
        user.setName("aa");
        user.setPassword("bb");
        user.setSalt("aa");
        user.setHeadUrl("aa");
        userDao.addUser(user);
    }

    @Test
    void testQuestionDao(){
        Random random = new Random();
        for (int i = 0; i < 11; ++i) {
            User user = new User();
            user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
            user.setName(String.format("USER%d", i));
            user.setPassword("");
            user.setSalt("");
            userDao.addUser(user);

            user.setPassword("newpassword");
            userDao.updatePassword(user);
            Question question = new Question();
            question.setCommentCount(i);
            Date date = new Date();
            date.setTime(date.getTime() + 1000 * 3600 * 5 * i);
            question.setCreatedDate(date);
            question.setUserId(user.getId());
            question.setTitle(String.format("TITLE{%d}", i));
            question.setContent(String.format("Balaababalalalal Content %d", i));
            questionDao.addQuestion(question);
        }
        System.out.println(questionDao.selectLatestQuestions(0, 5, 5));
    }

}
