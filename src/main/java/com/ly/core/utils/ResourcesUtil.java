package com.ly.core.utils;

import org.springframework.core.env.Environment;

/**
 * @Author: luoy
 * @Date: 2019/9/16 15:52.
 */
public class ResourcesUtil {
    private static Environment environment = SpringContextUtil.getBean(Environment.class);

    public static String getProp(String key) {
        return environment.getProperty(key);
    }

    public static Boolean getPropBoolean(String key) {
        return Boolean.valueOf(environment.getProperty(key));
    }

    public static int getPropInt(String key) {
        return Integer.valueOf(environment.getProperty(key));
    }
}

