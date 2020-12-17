package com.ly.core.utils;

import java.util.List;
import java.util.Map;

/**
 * @Author: luoy
 * @Date: 2020/4/22 14:47.
 */
public class ParameterizationUtil {

    public static String wildcardMatcherString(String data) {
        return PatternUtil.replace(data);
    }

    public static void wildcardMatcherHeadersAndRequests(Map<String, Object> data) {
        if (data == null || data.size() == 0) {
            return;
        }
        for (String key : data.keySet()) {
            if (data.get(key) instanceof String) {
                data.put(key, PatternUtil.replace((String) data.get(key)));
            }
        }
    }

    public static void wildcardMatcherValidate(Map<String, List<Object>> validate) {
        if (validate == null || validate.size() == 0) {
            return;
        }
        for (String key : validate.keySet()) {
            List<Object> lists = validate.get(key);
            if (lists != null && lists.size() > 0) {
                for (int i = 0; i < lists.size(); i++) {
                    if (lists.get(i) instanceof Map) {
                        Map<String, Object> map = (Map<String, Object>) lists.get(i);
                        for (String mapKey : map.keySet()) {
                            if (map.get(mapKey) instanceof String) {
                                map.put(mapKey, PatternUtil.replace((String) map.get(mapKey)));
                            }
                        }
                    }

                    if (lists.get(i) instanceof String) {
                        String arg = PatternUtil.replace((String) lists.get(i));
                        lists.set(i, arg);
                    }
                }
            }
        }
    }
}
