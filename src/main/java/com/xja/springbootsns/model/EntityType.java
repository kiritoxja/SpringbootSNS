package com.xja.springbootsns.model;

/**
 *
 **/

public class EntityType {
    public static final int ENTITY_QUESTION = 1;
    public static final int ENTITY_COMMENT = 2;

    public static String getEntityTypeName(int entityType){
        switch (entityType){
            case ENTITY_QUESTION :
                return "问题";
            case ENTITY_COMMENT:
                return "评论";
            default:
                return "未知类型";
        }
    }
}
