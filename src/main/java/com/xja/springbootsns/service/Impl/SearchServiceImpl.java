package com.xja.springbootsns.service.Impl;

import com.xja.springbootsns.model.Question;
import com.xja.springbootsns.service.serviceInterface.QuestionService;
import com.xja.springbootsns.service.serviceInterface.SearchService;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 **/
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    QuestionService questionServiceImpl;

    private static final String SOLR_URL = "http://127.0.0.1:8983/solr/wenda";
    private HttpSolrClient solrServer = new HttpSolrClient.Builder(SOLR_URL).build();
    private static final String QUESTION_TITLE_FIELD = "question_title";
    private static final String QUESTION_CONTENT_FIELD = "question_content";


    //返回关键字查找出的问题
    @Override
    public List<Question> searchQuestion(String keyword, int offset, int count) {
        List<Question> questionList = new ArrayList<>();
        SolrQuery query = new SolrQuery();
        //创建查询条件
        query.set("q",QUESTION_TITLE_FIELD+":"+keyword+" || "+QUESTION_CONTENT_FIELD+":"+keyword);
        query.setStart(offset);
        // 设置每页显示记录数
        query.setRows(count);
        // 调用server的查询方法，查询索引库
        QueryResponse response = null;
        try {
            response = solrServer.query(query);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
        if(null != response){
            //结果根据主键排序
            SolrDocumentList results = response.getResults();
            for (SolrDocument solrDocument : results) {
                //获得问题的主键
                int id = Integer.valueOf((String)solrDocument.get("id"));
                Question question = questionServiceImpl.getQuestionById(id);
                if(null != question){
                    questionList.add(question);
                }
            }
        }
        return questionList;
    }

    //对问题添加索引
    @Override
    public boolean indexQuestion(int qid, String title, String content) {
        SolrInputDocument doc =  new SolrInputDocument();
        doc.setField("id", qid);
        doc.setField(QUESTION_TITLE_FIELD, title);
        doc.setField(QUESTION_CONTENT_FIELD, content);
        UpdateResponse response = null;
        try {
            response = solrServer.add(doc, 1000);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
        return response != null && response.getStatus() == 0;
    }
}
