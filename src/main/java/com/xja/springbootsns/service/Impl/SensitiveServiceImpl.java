package com.xja.springbootsns.service.Impl;

import com.xja.springbootsns.service.serviceInterface.SensitiveService;
import org.apache.commons.lang3.CharUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *敏感词过滤服务
 **/
@Service
public class SensitiveServiceImpl implements SensitiveService, InitializingBean {

    private Logger logger = LoggerFactory.getLogger(SensitiveServiceImpl.class);
    private final String replacement = "*"; //敏感单字的替换符
    private TreeNode root = new TreeNode(); //敏感前缀树根节点

    private class TreeNode {
        private boolean isEnd = false; //是否为敏感词结尾
        private Map<Character, TreeNode> subNodes = new HashMap<>();

        //在当前位置添加子节点
        void addSubNode(Character key, TreeNode subNode){
            subNodes.put(key, subNode);
        }

        TreeNode getSubNode(Character key){
            return subNodes.get(key);
        }
    }

    //敏感词过滤
    @Override
    public String filter(String text) {
        if(StringUtils.isEmpty(text)){
            return text;
        }
        int begin = 0; //指示当前词开始位置
        int position = 0; //指示当前匹配位置
        int length = text.length();
        StringBuilder result = new StringBuilder();//存放过滤结果
        TreeNode currentNode = root; //当前树节点位置

        while(begin < length && position < length){
            Character currentChar = text.charAt(position);
            if(isSymbol(currentChar)){
                position++;
                continue;
            }
            currentNode = currentNode.getSubNode(currentChar);
            //当前字符不存在与前缀树了,即不存在begin开头的敏感词
            if(null == currentNode){
                result.append(text.charAt(begin));
                begin++;
                position = begin;
                currentNode = root;
            }else if(currentNode.isEnd){
                //这是一个敏感词
                while(begin <= position){
                    result.append(replacement);
                    begin++;
                }
                position = begin;
                currentNode = root;
            }else{
                //继续匹配
                position ++;
            }
        }
        return result.toString();
    }

    //防止敏感词中间加符号来跳过敏感过滤 因此跳过符号
    public boolean isSymbol(Character character){
        int intChar = (int) character;
        // 0x2E80-0x9FFF 东亚文字范围
        //不是东亚文字也不是符号就要跳过
        return !CharUtils.isAsciiAlphanumeric(character) && (intChar < 0x2E80 || intChar > 0x9FFF);
    }

    //在前缀树中添加敏感词
    private void addWord(String text){
        if(StringUtils.isEmpty(text)) return;
        TreeNode currentNode = root;
        for(Character character : text.toCharArray()){
            if(isSymbol(character)) continue;
            TreeNode tempNode = currentNode.getSubNode(character);
            if(null == tempNode){
                tempNode = new TreeNode();
                currentNode.addSubNode(character, tempNode);
            }
            currentNode = tempNode;
        }
        currentNode.isEnd = true;
    }

    //读取敏感词文件 构建前缀树
    @Override
    public void afterPropertiesSet() throws Exception {
        try(BufferedReader reader = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("SensitiveWords.txt"))))) {
            String line = "";
            while((line = reader.readLine()) != null){
                line = line.trim();
                addWord(line);
            }
        }catch (Exception e){
            logger.error("读取敏感词文件出错："+ e.getMessage());
        }
    }

    public static void main(String[] argv) {
        SensitiveServiceImpl s = new SensitiveServiceImpl();
        s.addWord("色情");
        s.addWord("好色");
        System.out.print(s.filter("你好X色**情XX"));
    }
}
