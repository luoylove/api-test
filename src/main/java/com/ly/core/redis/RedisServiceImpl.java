package com.ly.core.redis;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author luoy
 */
public class RedisServiceImpl implements RedisService{
  private RedisTemplate<String, Object> redisTemplate;

  public RedisServiceImpl(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  @Override
  public void set(String key, Object data) {
    redisTemplate.opsForValue().set(key, data);
  }

  @Override
  public void set(String key, Object data, Long expireTime) {
    if(expireTime > 0) {
      this.set(key, data);
      this.setExpire(key, expireTime);
    }
  }

  @Override
  public long getExpire(String key) {
    return redisTemplate.getExpire(key,TimeUnit.SECONDS);
  }

  @Override
  public Boolean exists(String key) {
    return redisTemplate.hasKey(key);
  }

  @Override
  public Object get(String key) {
    return key == null ?null : redisTemplate.opsForValue().get(key);
  }

  @Override
  public void del(String key) {
    if(exists(key)) {
      redisTemplate.delete(key);
    }
  }

  @Override
  public void delPattern(String kePattern) {
    Set<String> setKey = redisTemplate.keys(kePattern);

    if(setKey.size() > 0) {
      redisTemplate.delete(setKey);
    }
  }

  @Override
  public void setExpire(String key, long expireTime) {
    redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
  }

  @Override
  public void del(String... keys) {
    for (String key : keys) {
      if(exists(key)) {
        redisTemplate.delete(key);
      }
    }
  }

  @Override
  public void hmSet(String key, Map<Object, Object> map) {
    redisTemplate.opsForHash().putAll(key, map);
  }

  @Override
  public void hmSet(String key, Map<Object, Object> map, Long expireTime) {
    this.hmSet(key, map);
    if (expireTime > 0) {
      setExpire(key, expireTime);
    }
  }

  @Override
  public Map<Object, Object> hmGet(String key) {
    return redisTemplate.opsForHash().entries(key);
  }

  @Override
  public void hSet(String key, String hashKey, Object v) {
    redisTemplate.opsForHash().put(key, hashKey, v);
  }

  @Override
  public void hSet(String key, String hashKey, Object v, Long expireTime) {
    redisTemplate.opsForHash().put(key, hashKey, v);
    if(expireTime > 0) {
      setExpire(key, expireTime);
    }
  }

  @Override
  public Object hGet(String key, String hashKey) {
    return redisTemplate.opsForHash().get(key, hashKey);
  }

  @Override
  public void hDel(String key, String...hashKey) {
    if (key != null && hashKey != null) {
      redisTemplate.opsForHash().delete(key, (Object)hashKey);
    }
  }

  @Override
  public boolean existsHashKey(String key, String hashKey) {
    return redisTemplate.opsForHash().hasKey(key, hashKey);
  }
}
