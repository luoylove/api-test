package com.ly.core.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.lang3.StringUtils;

public class JSONSerializerUtil {

  /**深拷贝*/
  public static <T> T copy(T t, Class<T> clazz) {
    return unSerialize(serializeExistNull(t), clazz);
  }

  /**
   * 过滤value为null的key
   * @param t
   * @return
   */
  public static <T> String serialize(T t) {
    if (t == null) {
      return "";
    } else {
      return JSON.toJSONString(t);
    }
  }

  public static <T> Object serializeObj(T t) {
    if (t == null) {
      return "";
    } else {
      return JSONObject.toJSON(t);
    }
  }

  public static <T> T unSerialize(String json, Class<T> clazz) {
    if (StringUtils.isBlank(json)) {
      return null;
    } else {
      return JSON.parseObject(json, clazz);
    }
  }

  /**
   * 不过滤value为null的key
   * @param t
   * @return
   */
  public static <T> String serializeExistNull(T t) {
    if (t == null) {
      return "";
    } else {
      return JSON.toJSONString(t, SerializerFeature.WriteMapNullValue);
    }
  }

  /**
   * entity -> jsonobject
   * @param t
   * @param <T>
   * @return
   */
  public static <T> JSONObject serializeToJsonobject(T t) {
    if (t instanceof JSONObject) {
      return (JSONObject) t;
    }

    return (JSONObject) JSON.toJSON(t);
  }
}
