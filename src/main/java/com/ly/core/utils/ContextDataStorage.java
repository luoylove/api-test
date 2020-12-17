package com.ly.core.utils;

import org.testng.collections.Maps;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: luoy
 * @Date: 2019/9/2 17:58.
 */
public class ContextDataStorage {
    private final static String ASK_LABEL = "___ASK";

    /**
     * 作用域该testcase(method)
     *
     */
    private final static String METHOD_LABEL = "___METHOD";

    /**
     * 作用域该class
     */
    private final static String CLASS_LABEL = "___CLASS";

    /**
     * 作用域该suitecase(当前线程运行的所有用例)
     */
    private final static String THREAD_LABEL = "___THREAD";

    private static Map<String, Object> ContextDataStorageMap = Maps.newConcurrentMap();

    private static ThreadLocal<Map<String, Object>> localData = new ThreadLocal<>();

    private volatile static ContextDataStorage contextDataStorage = null;

    private ContextDataStorage(){}

    public static ContextDataStorage getInstance() {
        if (contextDataStorage == null) {
            synchronized (ContextDataStorage.class) {
                if (contextDataStorage == null) {
                    contextDataStorage = new ContextDataStorage();
                }
            }
        }
        return contextDataStorage;
    }

    /**
     * 存入一个作用域在整个测试的缓存
     * @param k
     * @param v
     */
    public void setAttribute(String k, Object v) {
        ContextDataStorageMap.put(k, v);
    }

    /**
     * 取出一个作用域在整个项目的缓存
     * @param k
     */
    public Object getAttribute(String k) {
        return ContextDataStorageMap.get(k);
    }

    /**
     * 取缓存, 优先: ask -> method-> class-> Thread->project
     *
     * @param k
     * @return
     */
    public Object getAttr(String k) {
        if (getAskAttribute(k) != null) {
            return getAskAttribute(k);
        }

        if (getMethodAttribute(k) != null) {
            return getMethodAttribute(k);
        }

        if (getClassAttribute(k) != null) {
            return getClassAttribute(k);
        }

        return getThreadAttribute(k) != null ? getThreadAttribute(k) : getAttribute(k);
    }

    public Boolean isExist(String k) {
        if (ContextDataStorageMap.containsKey(k)) {
            return true;
        }
        Map<String, Object> testLocalMap = localData.get();
        return testLocalMap != null ? testLocalMap.containsKey(k + METHOD_LABEL)
                || testLocalMap.containsKey(k + CLASS_LABEL)
                || testLocalMap.containsKey(k + THREAD_LABEL)
                || testLocalMap.containsKey(k + ASK_LABEL) : false;
    }

    public Set<String> getAttributeKeys() {
        HashSet<String> keys = new HashSet<>(ContextDataStorageMap.keySet());
        Map<String, Object> testLocalMap = localData.get();
        if (testLocalMap != null) {
            keys.addAll(testLocalMap.keySet().stream()
                    .map(k -> k.replace(THREAD_LABEL, ""))
                    .map(k -> k.replace(CLASS_LABEL, ""))
                    .map(k -> k.replace(METHOD_LABEL, ""))
                    .map(k -> k.replace(ASK_LABEL, ""))
                    .collect(Collectors.toSet()));
        }
        return keys;
    }

    public void removeAttribute(String k) {
        ContextDataStorageMap.remove(k);
        Map<String, Object> testLocalMap = localData.get();
        if (testLocalMap != null) {
            testLocalMap.remove(k + METHOD_LABEL);
            testLocalMap.remove(k + CLASS_LABEL);
            testLocalMap.remove(k + THREAD_LABEL);
        }
    }

    /**
     * 存入一个作用域在该请求级别的缓存
     * @param k
     * @param v
     */
    public void setAskAttribute(String k, Object v) {
        Map<String, Object> testLocalMap = localData.get();
        if (testLocalMap == null) {
            testLocalMap = Maps.newConcurrentMap();
        }
        testLocalMap.put(k + ASK_LABEL, v);
        localData.set(testLocalMap);
    }

    /**
     * 取出一个作用域在该请求级别的缓存
     * @param k
     */
    public Object getAskAttribute(String k) {
        return localData.get() != null ? localData.get().get(k + ASK_LABEL): null;
    }

    public void removeAllAskAttribute() {
        Map<String, Object> testLocalMap = localData.get();
        if (testLocalMap != null) {
            testLocalMap.forEach((k, v) -> {
                if (k.contains(ASK_LABEL)) {
                    testLocalMap.remove(k);
                }
            });
        }
    }

    /**
     * 存入一个作用域在该测试方法的缓存
     * @param k
     * @param v
     */
    public void setMethodAttribute(String k, Object v) {
        Map<String, Object> testLocalMap = localData.get();
        if (testLocalMap == null) {
            testLocalMap = Maps.newConcurrentMap();
        }
        testLocalMap.put(k + METHOD_LABEL, v);
        localData.set(testLocalMap);
    }

    /**
     * 取出一个作用域在该测试方法的缓存
     * @param k
     */
    public Object getMethodAttribute(String k) {
        return localData.get() != null ? localData.get().get(k + METHOD_LABEL): null;
    }

    public void removeAllMethodAttribute() {
        Map<String, Object> testLocalMap = localData.get();
        if (testLocalMap != null) {
            testLocalMap.forEach((k, v) -> {
                if (k.contains(METHOD_LABEL)) {
                    testLocalMap.remove(k);
                }
            });
        }
    }

    /**
     * 存入一个作用域在class的缓存
     * @param k
     * @param v
     */
    public void setClassAttribute(String k, Object v) {
        Map<String, Object> testLocalMap = localData.get();
        if (testLocalMap == null) {
            testLocalMap = Maps.newConcurrentMap();
        }
        testLocalMap.put(k + CLASS_LABEL, v);
        localData.set(testLocalMap);
    }

    /**
     * 取出一个作用域在class的缓存
     * @param k
     */
    public Object getClassAttribute(String k) {
        return localData.get() != null ? localData.get().get(k + CLASS_LABEL): null;
    }

    public void removeAllClassAttribute() {
        Map<String, Object> testLocalMap = localData.get();
        if (testLocalMap != null) {
            testLocalMap.forEach((k, v) -> {
                if (k.contains(CLASS_LABEL)) {
                    testLocalMap.remove(k);
                }
            });
        }
    }

    /**
     * 存入一个作用域在当前线程运行的所有缓存
     * @param k
     * @param v
     */
    public void setThreadAttribute(String k, Object v) {
        Map<String, Object> testLocalMap = localData.get();
        if (testLocalMap == null) {
            testLocalMap = Maps.newConcurrentMap();
        }
        testLocalMap.put(k + THREAD_LABEL, v);
        localData.set(testLocalMap);
    }

    /**
     * 删除作用域在当前线程运行的所有缓存
     */
    public void removeAllThreadAttribute() {
        localData.remove();
    }

    /**
     * 取出一个作用域在当前线程运行的所有缓存
     * @param k
     */
    public Object getThreadAttribute(String k) {
        return localData.get() != null ? localData.get().get(k + THREAD_LABEL): null;
    }
}
