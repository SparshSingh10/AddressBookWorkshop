package com.example.AddressBookWorkshop.controller.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis-test")
public class RedisTestController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/ping")
    public String pingRedis() {
        try {
            redisTemplate.opsForValue().set("testKey", "Redis is working!");
            return "Redis Connection Successful: " + redisTemplate.opsForValue().get("testKey");
        } catch (Exception e) {
            return "Redis Connection Failed: " + e.getMessage();
        }
    }
}
