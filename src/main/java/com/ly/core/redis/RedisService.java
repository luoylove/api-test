package com.ly.core.redis;

import java.util.Map;

public interface RedisService {

  /**
   * 写入redis
   * @param key
   * @param data
   * @return
   */
  void set(String key, Object data);

  /**
   * 写入redis 设置过时时间
   * @param key
   * @param data
   * @param expireTime
   * @return
   */
  void set(String key, Object data, Long expireTime);

  /**
   * 判断key是否存在
   * @param key
   * @return
   */
  Boolean exists(String key);

  /**
   * 获取key过期时间
   * @param key
   * @return
   */
  long getExpire(String key);

  /**
   * 设置过期时间
   * @param key
   * @param expireTime
   */
  void setExpire(String key, long expireTime);

  /**
   * 获取数据
   * @param key
   * @return
   */
  Object get(String key);

  /**
   * 删除key
   * @param key
   */
  void del(String key);

  /**
   * 批量删除满足正则的key
   * @param kePattern
   */
  void delPattern(String kePattern);

  /**
   * 删除多个key
   * @param keys
   */
  void del(String... keys);

  /**
   * 添加hash
   * @param key
   * @param map
   */
  void hmSet(String key, Map<Object, Object> map);

  /**
   * 添加hash，设置过期时间
   * @param key
   * @param map
   * @param expireTime
   */
  void hmSet(String key, Map<Object, Object> map, Long expireTime);

  /**
   * 获取hash
   * @param key
   * @return  Map<String, Object>
   */
  Map<Object, Object> hmGet(String key);

  /**
   * 向一张hash表中放入数据,如果不存在将创建
   * @param key
   * @param hashKey
   * @param v
   */
  void hSet(String key, String hashKey, Object v);

  /**
   * 向一张hash表中放入数据,如果不存在将创建,设置过期时间,过期时间会覆盖原来设置过期时间
   * @param key
   * @param hashKey
   * @param v
   * @param expireTime
   */
  void hSet(String key, String hashKey, Object v, Long expireTime);

  /**
   * 获取hash表中的key对应的value
   * @param key
   * @param hashKey
   * @return
   */
  Object hGet(String key, String hashKey);

  /**
   * 删除hash表中对应的key
   * @param key
   * @param hashKey
   */
  void hDel(String key, String... hashKey);

  /**
   * 判断hash表中是否存在该key
   * @param key
   * @param hashKey
   * @return
   */
  boolean existsHashKey(String key, String hashKey);

  //list
  //set
}




















