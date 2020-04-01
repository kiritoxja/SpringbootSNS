package com.xja.springbootsns.controller;

import com.xja.springbootsns.model.EntityType;
import com.xja.springbootsns.model.Question;
import com.xja.springbootsns.model.ViewObject;
import com.xja.springbootsns.service.serviceInterface.FollowService;
import com.xja.springbootsns.service.serviceInterface.QuestionService;
import com.xja.springbootsns.service.serviceInterface.SearchService;
import com.xja.springbootsns.service.serviceInterface.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchController {
    @Autowired
    SearchService searchServiceImpl;
    @Autowired
    FollowService followServiceImpl;
    @Autowired
    UserService userServiceImpl;
    @Autowired
    QuestionService questionServiceImpl;

    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    @GetMapping(value = "/search")
    public String search(Model model, @RequestParam("q") String keyword,
                         @RequestParam(value = "offset", defaultValue = "0") int offset,
                         @RequestParam(value = "count", defaultValue = "10") int count) {
        try {
            List<Question> questionList = searchServiceImpl.searchQuestion(keyword, offset,count);
            List<ViewObject> vos = new ArrayList<>();
            for (Question question : questionList) {
                ViewObject vo = new ViewObject();
                vo.put("question", question);
                vo.put("followCount", followServiceImpl.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
                vo.put("user", userServiceImpl.getUserById(question.getUserId()));
                vos.add(vo);
            }
            model.addAttribute("vos", vos);
            model.addAttribute("keyword", keyword);
        } catch (Exception e) {
            logger.error("搜索失败" + e.getMessage());
        }
        return "result";
    }
}