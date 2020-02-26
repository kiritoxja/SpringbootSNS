package com.xja.springbootsns.dao;

import com.xja.springbootsns.model.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface MessageDao {
    String TABLE_NAME = " message ";
    String INSERT_FIELDS = " from_id, to_id, content, has_read, conversation_id, created_date ";
    String INSERT_VALUES=" #{fromId}, #{toId}, #{content}, #{hasRead}, #{conversationId}, #{createdDate}";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    //添加一条私信
    @Insert(value = {"insert into ", TABLE_NAME, " ( ", INSERT_FIELDS, " ) values (", INSERT_VALUES, ")"})
    int addMessage(Message message);

    //查找某个会话的所有私信
    @Select(value = {"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where conversation_id=#{conversationId} order by id desc limit #{offset}, #{limit}"})
    List<Message> getConversationDetail(@Param("conversationId") String conversationId,
                                        @Param("offset") int offset, @Param("limit") int limit);

    //查找某个用户的某个会话的所有未读私信的数量
    @Select(value = {"select count(id) from ", TABLE_NAME,
            "where conversation_id=#{conversationId} and to_id=#{userId} and has_read=0"})
    int getConversationUnreadMessageCount(String conversationId, int userId);

    //查找某个用户的所有会话（以该会话的最新私信为代表,同时将查到的每个会话的私信数作为返回的私信的id
    //注意在子查询中使用DISTINCT来触发DERIVED 操作，才能让数据先排序再取第一个
    @Select(value = {"select ", INSERT_FIELDS, " ,count(id) as id from ( select ", " DISTINCT(id), ", INSERT_FIELDS, " from ", TABLE_NAME,
            " where from_id=#{userId} or to_id=#{userId} order by id desc) tt group by conversation_id order by created_date desc limit #{offset}, #{limit}"})
    List<Message> getConversationListByUserId(int userId, int offset, int limit);

    //修改私信的未读状态
    @Update(value = {"update ", TABLE_NAME, " set has_read=1 where conversation_id=#{conversationId}"})
    void updateHasRead(String conversationId);
}
