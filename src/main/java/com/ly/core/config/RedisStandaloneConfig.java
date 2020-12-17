package com.ly.core.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

// 因为配置文件不对,注释掉Configuration, 避免启动失败
//@Configuration
//@Lazy
public class RedisStandaloneConfig {
    @Value("${spring.redis.standalone.host}")
    private String host;

    @Value("${spring.redis.standalone.port}")
    private int port;

    @Value("${spring.redis.standalone.password}")
    private String password;

    @Value("${spring.redis.standalone.timeout}")
    private int timeout;

    @Value("${spring.redis.standalone.database}")
    private int database;

    @Bean(name = "redisStandaloneTemplate")
    public RedisTemplate<String, Object> redisTemplate(@Qualifier("redisStandaloneConnectionFactory") RedisConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        RedisStandaloneConfig.initSerializerTemplate(redisTemplate, factory);
        return redisTemplate;
    }

    @Bean(name = "redisStandaloneConnectionFactory")
    public RedisConnectionFactory connectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPassword(RedisPassword.of(password));
        configuration.setDatabase(database);
        configuration.setPort(port);
        JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfigurationBuilder = JedisClientConfiguration.builder();
        jedisClientConfigurationBuilder.connectTimeout(Duration.ofMillis(timeout));
        return new JedisConnectionFactory(configuration, jedisClientConfigurationBuilder.build());
    }

    /**
     * redis 序列化 ，存储关键字是字符串 ，值是Jdk序列化
     * @param redisTemplate
     * @param factory
     */
    public static <T> void initSerializerTemplate(RedisTemplate<String, T> redisTemplate, RedisConnectionFactory factory) {
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objectMapper.registerModule(new Jdk8Module());
        GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        redisTemplate.setKeySerializer(redisSerializer);
        redisTemplate.setHashKeySerializer(redisSerializer);
        redisTemplate.setValueSerializer(redisSerializer);
        redisTemplate.setHashValueSerializer(redisSerializer);
//        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
//        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.setConnectionFactory(factory);
    }
}
