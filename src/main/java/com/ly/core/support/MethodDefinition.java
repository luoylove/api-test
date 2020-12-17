package com.ly.core.support;

import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * @Author: luoy
 * @Date: 2020/12/4 13:30.
 */
@Data
@Builder
public class MethodDefinition {
    private Method method;

    private Object[] args;

    private Object instance;
}
