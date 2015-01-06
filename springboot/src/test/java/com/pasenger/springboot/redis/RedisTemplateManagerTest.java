package com.pasenger.springboot.redis;

import com.pasenger.springboot.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 * Created by Pasenger on 2015/1/6.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class RedisTemplateManagerTest {
    @Autowired
    private RedisTemplateManager redisTemplateManager;

    @Test
    public void TestRedisTemplate(){
        redisTemplateManager.createRedisTemplate("192.168.100.10", 6381);
        redisTemplateManager.createRedisTemplate("192.168.100.10", 6382);

        RedisTemplate redisTemplate1 = redisTemplateManager.getRedisTemplate("192.168.100.10", 6381);
        redisTemplate1.setKeySerializer(new StringRedisSerializer());
        redisTemplate1.setValueSerializer(new StringRedisSerializer());

        final byte[] key = redisTemplate1.getKeySerializer().serialize("abc");
        final byte[] value = redisTemplate1.getValueSerializer().serialize("123");
        final byte[] _value = redisTemplate1.getValueSerializer().serialize("1234");

        redisTemplate1.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.set(key, value);
                return null;
            }
        });

        RedisTemplate redisTemplate2 = redisTemplateManager.getRedisTemplate("192.168.100.10", 6382);
        redisTemplate2.setKeySerializer(new StringRedisSerializer());
        redisTemplate2.setValueSerializer(new StringRedisSerializer());
        redisTemplate2.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.set(key, _value);
                return null;
            }
        });

        byte[] value1 = (byte[]) redisTemplate1.execute(new RedisCallback<byte[]>() {
            @Override
            public byte[] doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.get(key);
            }
        });

        String val1 = (String) redisTemplate1.getValueSerializer().deserialize(value1);

        byte[] value2 = (byte[]) redisTemplate2.execute(new RedisCallback<byte[]>() {
            @Override
            public byte[] doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.get(key);
            }
        });

        String val2 = (String) redisTemplate2.getValueSerializer().deserialize(value2);

        RedisTemplate redisTemplate3 = redisTemplateManager.getRedisTemplate("192.168.100.10", 6383);
        redisTemplate3.setKeySerializer(new StringRedisSerializer());
        redisTemplate3.setValueSerializer(new StringRedisSerializer());
        redisTemplate3.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.set(key, _value);
                return null;
            }
        });

        byte[] value3 = (byte[]) redisTemplate3.execute(new RedisCallback<byte[]>() {
            @Override
            public byte[] doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.get(key);
            }
        });

        String val3 = (String) redisTemplate1.getValueSerializer().deserialize(value3);

        RedisTemplate redisTemplate4 = redisTemplateManager.getRedisTemplate("192.168.100.10", 6384);

        assertEquals(3, redisTemplateManager.getRedisTemplateCount());
        assertEquals("123", val1);
        assertEquals("1234", val2);
        assertEquals("1234", val3);

    }
}
