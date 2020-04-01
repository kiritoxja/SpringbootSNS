package com.xja.springbootsns.service.serviceInterface;

import com.xja.springbootsns.model.Question;

import java.util.List;

public interface SearchService {

    List<Question> searchQuestion(String keyword, int offset, int count);

    boolean indexQuestion(int qid, String title, String content);
}
