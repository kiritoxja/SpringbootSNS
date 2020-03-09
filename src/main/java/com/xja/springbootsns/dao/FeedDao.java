package com.xja.springbootsns.dao;

import com.xja.springbootsns.model.Feed;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface FeedDao {
    String TABLE_NAME = " feed ";
    String INSERT_FIELDS = " user_id, data, created_date, type ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS, ") values (#{userId},#{data},#{createdDate},#{type})"})
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    int addFeed(Feed feed);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    Feed getFeedById(int id);

    //筛选出用户们的不大于maxId的新鲜事
    List<Feed> selectUserFeeds(@Param("maxId") int maxId, @Param("userIds")List<Integer> userIds,@Param("count") int count);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where user_id=#{userId}"})
    List<Feed> selectFeedsByUserId(int userId);
}
