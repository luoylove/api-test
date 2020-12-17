package com.ly.core.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.MapPropertySource;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.Map;


/**
 * redis cluster config
 */
// 因为配置文件不对,注释掉Configuration, 避免启动失败
//@Configuration
//@Lazy
public class RedisClusterConfig {
    @Value("${spring.redis.cluster.nodes}")
    private String clusterNodes;

    @Value("${spring.redis.cluster.timeout}")
    private Long timeout;

    @Value("${spring.redis.cluster.password}")
    private String auth;

    @Bean(name = "redisClusterConfiguration")
    public RedisClusterConfiguration getClusterConfiguration() {
        Map<String, Object> config = new HashMap<>();
        config.put("spring.redis.cluster.nodes", clusterNodes.trim());
        config.put("spring.redis.cluster.timeout", timeout);
//    config.put("spring.redis.cluster.password", auth);
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(new MapPropertySource("RedisClusterConfiguration", config));
        redisClusterConfiguration.setPassword(RedisPassword.of(auth));
        return redisClusterConfiguration;
    }

    @Bean(name = "redisClusterPoolConfig")
    public JedisPoolConfig getJedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        return jedisPoolConfig;
    }

    @Bean(name = "redisClusterConnectionFactory")
    public RedisConnectionFactory connectionFactory(@Qualifier("redisClusterConfiguration") RedisClusterConfiguration redisClusterConfig,
                                                    @Qualifier("redisClusterPoolConfig") JedisPoolConfig pooConfig) {

        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisClusterConfig, pooConfig);
        return jedisConnectionFactory;
    }

    /**
     * redis模板
     *
     * @param factory
     * @param <T>
     * @return
     */
    @Bean(name = "redisClusterTemplate")
    public <T> RedisTemplate<String, T> redisTemplate(@Qualifier("redisClusterConnectionFactory") RedisConnectionFactory factory) {
        RedisTemplate<String, T> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        RedisStandaloneConfig.initSerializerTemplate(redisTemplate, factory);
        return redisTemplate;
    }
}
