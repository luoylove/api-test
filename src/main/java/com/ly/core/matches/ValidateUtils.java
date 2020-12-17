package com.ly.core.matches;

import com.ly.core.config.DefaultConstantConfig;
import com.ly.core.utils.ReflectUtil;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author: luoy
 * @Date: 2020/4/21 13:09.
 */
public class ValidateUtils {
    public final static String METHOD_NAME_KEY = "method";
    public final static String METHOD_ARGS_KEY = "args";

    public static boolean eq(Object actual, Object expected) {
        if (actual == null) {
            return expected == null;
        }

        if (expected != null && isArray(actual)) {
            return isArray(expected) && areArraysEqual(actual, expected);
        }

        return actual.equals(expected);
    }

    public static boolean isNull(Object operand) {
        if (operand == null) {
            return true;
        }

        if (operand instanceof Map) {
            return ((Map) operand).size() == 0;
        }

        if (operand instanceof List) {
            return ((List) operand).size() == 0;
        }

        if (operand instanceof String) {
            return operand == "";
        }
        return false;
    }

    public static boolean notNull(Object operand) {
        return !isNull(operand);
    }

    public static boolean plugin(Map<String, Object> operand) {
        Object invoke = null;
        if (operand.get(METHOD_ARGS_KEY) instanceof String) {
            invoke = ReflectUtil.reflectInvoke(DefaultConstantConfig.PLUGIN_METHOD_BY_CLASS, (String) operand.get(METHOD_NAME_KEY), (String) operand.get(METHOD_ARGS_KEY));
        } else if (operand.get(METHOD_ARGS_KEY) instanceof Object[]) {
            Object[] args = (Object[]) operand.get(METHOD_ARGS_KEY);
            invoke = ReflectUtil.reflectInvoke(DefaultConstantConfig.PLUGIN_METHOD_BY_CLASS, (String) operand.get(METHOD_NAME_KEY), args);
        } else {
            invoke = ReflectUtil.reflectInvoke(DefaultConstantConfig.PLUGIN_METHOD_BY_CLASS, (String) operand.get(METHOD_NAME_KEY), "");
        }
        if (invoke != null && invoke instanceof Boolean) {
            return (boolean) invoke;
        }
        return true;
    }

    public static boolean plugin(String method, Object...args) {
        Object invoke = ReflectUtil.reflectInvoke(DefaultConstantConfig.PLUGIN_METHOD_BY_CLASS, method, args);
        if (invoke != null && invoke instanceof Boolean) {
            return (boolean) invoke;
        }
        return true;
    }

    public static boolean gt(BigDecimal actual, BigDecimal expected) {
        return actual.doubleValue() > expected.doubleValue();
    }

    public static boolean lt(BigDecimal actual, BigDecimal expected) {
        return actual.doubleValue() < expected.doubleValue();
    }

    public static boolean contains(List<Object> operand, Object expected) {
        return operand.contains(expected);
    }

    public static boolean hasKey(Map<Object, Object> operand, Object expected) {
        return operand.containsKey(expected);
    }

    public static boolean hasValue(Map<Object, Object> operand, Object expected) {
        return operand.containsValue(expected);
    }

    public static boolean len(Object operand, int expected) {
        if ( operand instanceof Map) {
            return ((Map) operand).size() == expected;
        }
        if (operand instanceof List ) {
            return ((List) operand).size() == expected;
        }

        return String.valueOf(operand).length() == expected;
    }

    private static boolean areArraysEqual(Object actualArray, Object expectedArray) {
        return areArrayLengthsEqual(actualArray, expectedArray) && areArrayElementsEqual(actualArray, expectedArray);
    }

    private static boolean areArrayLengthsEqual(Object actualArray, Object expectedArray) {
        return Array.getLength(actualArray) == Array.getLength(expectedArray);
    }

    private static boolean areArrayElementsEqual(Object actualArray, Object expectedArray) {
        for (int i = 0; i < Array.getLength(actualArray); i++) {
            if (!eq(Array.get(actualArray, i), Array.get(expectedArray, i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isArray(Object o) {
        return o.getClass().isArray();
    }
}
