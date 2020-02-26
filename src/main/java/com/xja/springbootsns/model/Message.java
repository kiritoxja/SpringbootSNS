package com.xja.springbootsns.model;

import org.springframework.web.util.HtmlUtils;

import java.util.Date;

public class Message {

  private int id;
  private int fromId;
  private int toId;
  private String content;
  //统一规定小id_大id 表示一个会话id
  private String conversationId;
  private int hasRead;
  private Date createdDate;


  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }


  public int getFromId() {
    return fromId;
  }

  public void setFromId(int fromid) {
    this.fromId = fromid;
  }


  public int getToId() {
    return toId;
  }

  public void setToId(int toId) {
    this.toId = toId;
  }


  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }


  public String getConversationId() {
    return conversationId;
  }

  public void setConversationId(String conversationId) {
    this.conversationId = conversationId;
  }


  public java.util.Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(java.util.Date createdDate) {
    this.createdDate = createdDate;
  }

  public int getHasRead() {
    return hasRead;
  }

  public void setHasRead(int hasRead) {
    this.hasRead = hasRead;
  }

  public String generateConversationId() {
    if (fromId < toId) {
      return String.format("%d_%d", fromId, toId);
    } else {
      return String.format("%d_%d", toId, fromId);
    }
  }

  public Message() {
  }

  public Message(int fromId, int toId, String content, Date createdDate) {
    this.fromId = fromId;
    this.toId = toId;
    this.content = content;
    this.conversationId = generateConversationId();
    this.hasRead = 0;
    this.createdDate = createdDate;
  }
}
