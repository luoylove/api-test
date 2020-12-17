package com.ly.core.actuator;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 动态代理的方式处理运行前与运行后
 * @Author: luoy
 * @Date: 2020/6/2 16:57.
 */
@Slf4j
public class TestNgActuatorHandler implements InvocationHandler {
    private TestNgActuator actuator;

    public TestNgActuatorHandler(TestNgActuator actuator) {
        this.actuator = actuator;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        Object result = null;
        try {
            before(method, args);
            result = method.invoke(actuator, args);
            after(result);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void before(Method method, Object[] args) {
        log.info("=====================runStart=====================");
        log.info("run method:{}, args: {}, time:{}", method.getName(), Arrays.toString(args), System.currentTimeMillis());
    }

    private void after(Object result) {
        log.info("=====================runEnd=====================");
    }
}
