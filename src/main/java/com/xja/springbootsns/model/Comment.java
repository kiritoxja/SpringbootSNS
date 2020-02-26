package com.xja.springbootsns.model;


import java.util.Date;

public class Comment {

  private int id;
  private String content;
  private int userId;
  private java.util.Date createdDate;
  private int entityId;
  private int entityType;
  private int status;


  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }


  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }


  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }


  public java.util.Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(java.util.Date createdDate) {
    this.createdDate = createdDate;
  }


  public int getEntityId() {
    return entityId;
  }

  public void setEntityId(int entityId) {
    this.entityId = entityId;
  }


  public int getEntityType() {
    return entityType;
  }

  public void setEntityType(int entityType) {
    this.entityType = entityType;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public Comment() {
  }

  public Comment(String content, int userId, Date createdDate, int entityId, int entityType, int status) {
    this.content = content;
    this.userId = userId;
    this.createdDate = createdDate;
    this.entityId = entityId;
    this.entityType = entityType;
    this.status = status;
  }
}
