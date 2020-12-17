package com.ly.core.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by luoy on 2019/7/17.
 */
public class MapTool {
    private Map<String, String> map = new HashMap<>();

    private MapTool put(String k, String v) {
        map.put(k, v);
        return this;
    }

    public static Builder builder() {
        return new Builder ();
    }

    public static class Builder {
        private MapTool mapTool = new MapTool();
        public Builder put(String k, String v) {
            mapTool.put(k, v);
            return this;
        }

        public Map<String, String> build() {
            return mapTool.map;
        }
    }
}


