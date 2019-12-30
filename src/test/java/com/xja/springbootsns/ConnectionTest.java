package com.xja.springbootsns;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import javax.sql.DataSource;

/**
 *
 **/
@SpringBootTest
public class ConnectionTest {

    @Autowired
    DataSource dataSource;


    @Test
    @Sql("/createTable.sql")
    void testConnection(){
        System.out.println(dataSource);
    }
}
