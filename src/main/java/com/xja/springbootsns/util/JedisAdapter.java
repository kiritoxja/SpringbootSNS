package com.xja.springbootsns.util;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;

/**
 * jedis 接口 封装pool
 **/
@Service
public class JedisAdapter implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);
    private JedisPool pool;
    private String host = "localhost";
    private int port = 6379;
    private String password = "199661";
    private int timeout = 2000;
    private int defaultDatabaseIndex = 10;

    //初始化Jedis连接池
    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool(new GenericObjectPoolConfig(),host,port,timeout,password,defaultDatabaseIndex);
    }

    //set API
    public long sadd(String key, String value){
        try (Jedis jedis = pool.getResource()){
            return jedis.sadd(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("发生异常" + e.getMessage());
        }
        return 0;
    }

    public long srem(String key, String value){
        try (Jedis jedis = pool.getResource()){
            return jedis.srem(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("发生异常" + e.getMessage());
        }
        return 0;
    }

    public long scard(String key){
        try (Jedis jedis = pool.getResource()){
            return jedis.scard(key);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("发生异常" + e.getMessage());
        }
        return 0;
    }

    public boolean sismember(String key, String value){
        try (Jedis jedis = pool.getResource()){
            return jedis.sismember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("发生异常" + e.getMessage());
        }
        return false;
    }

    //list API
    public long lpush(String key, String value){
        try (Jedis jedis = pool.getResource()){
            return jedis.lpush(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("发生异常" + e.getMessage());
        }
        return 0;
    }

    //移出并获取列表的最后一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。返回所属的key和value
    public List<String> brpop(int timeout, String key){
        try (Jedis jedis = pool.getResource()){
            return jedis.brpop(timeout,key);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("发生异常" + e.getMessage());
        }
        return null;
    }
}
