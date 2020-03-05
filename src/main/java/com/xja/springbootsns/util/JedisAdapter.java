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
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.util.List;
import java.util.Set;

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

    public Jedis getJedis() {
        return pool.getResource();
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

    //zset api
    public Set<String> zrevrange(String key, int start, int end) {
        try (Jedis jedis = pool.getResource()){
            return jedis.zrevrange(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("发生异常" + e.getMessage());
        }
        return null;
    }

    //元素个数
    public long zcard(String key) {
        try (Jedis jedis = pool.getResource()){
            return jedis.zcard(key);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("发生异常" + e.getMessage());
        }
        return 0;
    }

    //元素的score
    public Double zscore(String key, String member) {
        try (Jedis jedis = pool.getResource()){
            return jedis.zscore(key, member);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("发生异常" + e.getMessage());
        }
        return null;
    }


    //事务
    public Transaction multi(Jedis jedis) {
        try {
            return jedis.multi();
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
        }
        return null;
    }

    //每一个object 表示操作的返回值
    public List<Object> exec(Transaction tx, Jedis jedis) {
        try {
            return tx.exec();
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            tx.discard();
        } finally {
            if (tx != null) {
                tx.close();
            }
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

}
