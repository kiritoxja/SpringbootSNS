package com.xja.springbootsns;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 *
 **/
@SpringBootTest
public class JedisTest {
    private static final Logger logger = LoggerFactory.getLogger(JedisTest.class);
    private JedisPool pool;
    private String host = "localhost";
    private int port = 6379;
    private String password = "199661";
    private int timeout = 2000;
    private int defaultDatabaseIndex = 0;

    public Long zinterstore(String dstkey, String... sets) {
        JedisPool pool = new JedisPool(new GenericObjectPoolConfig(),host,port,timeout,password,defaultDatabaseIndex);
        try (Jedis jedis = pool.getResource()){
            return jedis.zinterstore(dstkey,sets);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("发生异常" + e.getMessage());
        }
        return null;
    }

    @Test
    void test(){
        JedisPool pool = new JedisPool(new GenericObjectPoolConfig(),host,port,timeout,password,defaultDatabaseIndex);
        Jedis jedis = pool.getResource();
        String test1 = "test1";
        String test2 = "test2";
        String test3 = "test3";

        jedis.zadd(test1, 3, "3");
        jedis.zadd(test1, 5, "5");
        jedis.zadd(test1, 4, "4");
       /* for(String s : jedis.zrevrange(test1, 0, 2)){
            System.out.println(s);
        }*/

        jedis.zadd(test2, 3, "3");
        jedis.zadd(test2, 5, "5");

        this.zinterstore(test1,test1,test2);
        System.out.println(jedis.zrevrange(test1, 0, 1));
        System.out.println(jedis.zcard(test3));

    }
}
