package com.pasenger.springboot.redis;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.jredis.JredisConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Pasenger on 2015/1/6.
 */
@Slf4j
@Data
@Service
public class RedisTemplateManager {
    @Value("${redis.jedis.pool.max-total}")
    private int maxTotal;

    @Value("${redis.jedis.pool.max-idle}")
    private int maxIdle;

    @Value("${redis.jedis.pool.min-idle}")
    private int minIdle;

    @Value("${redis.jedis.pool.max-wait}")
    private int maxWait;

    @Value("${redis.jedis.pool.test-on-borrow}")
    private boolean testOnBorrow;

    /**
     * redisTemplate Map
     * key: redisName
     * value: RedisTemplate
     */
    private Map<String, RedisTemplate> redisTemplateMap = new HashMap<String, RedisTemplate>();

    /**
     * get redisTemplate count
     * @return
     */
    public int getRedisTemplateCount(){
        return redisTemplateMap.size();
    }

    /**
     * create redisTemplate
     * @param host
     * @param port
     */
    public RedisTemplate createRedisTemplate(String host, int port){
        String redisName = host + ":" + port;

        if(redisTemplateMap.containsKey(redisName)){
            log.info("redisTemplate is exists: {}", redisName);

            return this.getRedisTemplate(redisName);
        }

        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setPoolConfig(this.getJedisPoolConfig());

        jedisConnectionFactory.setHostName(host);
        jedisConnectionFactory.setPort(port);
        jedisConnectionFactory.afterPropertiesSet();

        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        redisTemplate.afterPropertiesSet();

        //检测是否连接成功
        try {
            redisTemplate.execute(new RedisCallback<Long>() {
                @Override
                public Long doInRedis(RedisConnection connection) throws DataAccessException {
                   return null;
                }
            });
        }catch (Exception e){
            log.error("create redisTemplate failed, host:{}, port:{}", host, port);
            e.printStackTrace();
            return null;
        }

        log.info("redisTeplate create success: host:{}, port:{}", host, port);


        redisTemplateMap.put(redisName, redisTemplate);

        return redisTemplate;
    }

    /**
     * Get RedisTemplate
     * @param host
     * @param port
     * @return
     */
    public RedisTemplate getRedisTemplate(String host, int port){
        String redisName = host + ":" + port;

        RedisTemplate redisTemplate = this.getRedisTemplate(redisName);

        if(redisTemplate == null){
            return this.createRedisTemplate(host, port);
        }

        return redisTemplate;
    }

    /**
     * get RedisTemplate
     * @param redisName
     * @return
     */
    public RedisTemplate getRedisTemplate(String redisName){
        if(redisTemplateMap.containsKey(redisName)){
            return redisTemplateMap.get(redisName);
        }

        return null;
    }

    /**
     * 获取JedisPoolConfig
     * @return
     */
    private JedisPoolConfig getJedisPoolConfig(){
        JedisPoolConfig config = new JedisPoolConfig();

        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setMaxWaitMillis(maxWait);
        config.setTestOnBorrow(testOnBorrow);

        return config;
    }
}
