package com.xja.springbootsns.dao;

import com.xja.springbootsns.model.User;
import org.apache.ibatis.annotations.*;

public interface UserDao {

    String TABLE_NAME = " user ";
    String INSERT_FIELDS=" name, password, salt, head_url ";
    String INSERT_VALUES=" #{name}, #{password}, #{salt}, #{headUrl}";
    String SELECT_FIELDS=" id, "+INSERT_FIELDS;

    @Insert(value = {"insert into ",TABLE_NAME,"(",INSERT_FIELDS,")"," values (",INSERT_VALUES,")"})
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    int addUser(User user);

    @Delete(value = {"delete from ",TABLE_NAME," where id=#{id}"})
    void deleteById(int id);

    @Update(value = {"update ",TABLE_NAME,"set password=#{password} where id=#{id}"})
    void updatePassword(User user);

    @Select(value = {"select ",SELECT_FIELDS, " from ",TABLE_NAME," where id=#{id}"})
    User selectById(int id);

    @Select(value = {"select ",SELECT_FIELDS, " from ",TABLE_NAME," where name=#{name}"})
    User selectByName(String name);
}
