package com.xja.springbootsns.controller;

import com.xja.springbootsns.model.Question;
import com.xja.springbootsns.model.ViewObject;
import com.xja.springbootsns.service.serviceInterface.QuestionService;
import com.xja.springbootsns.service.serviceInterface.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 *首页控制器
 **/

@Controller
public class IndexController {

    @Autowired
    QuestionService questionServiceImpl;

    @Autowired
    UserService userServiceImpl;

    private List<ViewObject> getQuestions(int userId, int offset, int limit){
        List<ViewObject> viewObjects = new ArrayList<>();
        List<Question> questionList = questionServiceImpl.getUserLatestQuestions(userId, offset, limit);
        for(Question question : questionList){
            ViewObject viewObject = new ViewObject();
            viewObject.put("question", question);
            viewObject.put("user", userServiceImpl.getUserById(question.getUserId()));
            viewObjects.add(viewObject);
        }
        return viewObjects;
    }
    @RequestMapping(value = {"/","/index","/index.html"})
    public String index(Model model){
        model.addAttribute("vos", getQuestions(0, 0, 10));
        return "index";
    }

    //查找某个用户发布的问题
    @RequestMapping("/user/{userId}")
    public String userIndex(Model model, @PathVariable("userId")int userId){
        model.addAttribute("vos", getQuestions(userId, 0, 10));
        return "index";
    }
}
