package com.xja.springbootsns.model;


public class Message {

  private int id;
  private int fromid;
  private int toid;
  private String content;
  private int conversationId;
  private java.util.Date createdDate;


  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }


  public int getFromid() {
    return fromid;
  }

  public void setFromid(int fromid) {
    this.fromid = fromid;
  }


  public int getToid() {
    return toid;
  }

  public void setToid(int toid) {
    this.toid = toid;
  }


  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }


  public int getConversationId() {
    return conversationId;
  }

  public void setConversationId(int conversationId) {
    this.conversationId = conversationId;
  }


  public java.util.Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(java.util.Date createdDate) {
    this.createdDate = createdDate;
  }

}
