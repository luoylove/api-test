package com.ly.core.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @Author: luoy
 * @Date: 2019/8/30 9:58.
 */
//@Configuration
//懒加载，当第一次使用这个bean的时候才加载进容器，目的为2点，
// 1：减少IOC容器启动时间
@Lazy
public class RedisConfig {
    @Bean("Standalone-redis")
    @Primary
    public RedisService createProRedis(@Qualifier("redisStandaloneTemplate")RedisTemplate<String, Object> redisTemplate) {
        return new RedisServiceImpl(redisTemplate);
    }

    @Bean("Cluster-redis")
    public RedisService createStandardRedis(@Qualifier("redisClusterTemplate")RedisTemplate<String, Object> redisTemplate) {
        return new RedisServiceImpl(redisTemplate);
    }
}
