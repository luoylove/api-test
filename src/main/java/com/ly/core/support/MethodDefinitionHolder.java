package com.ly.core.support;

import lombok.Builder;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: luoy
 * @Date: 2020/12/4 15:08.
 */
@Builder
public class MethodDefinitionHolder {
    private List<MethodDefinition> methodDefinitions;

    public List<MethodDefinition> byAnnotation(Class<? extends Annotation> annotation) {
        return methodDefinitions.stream().filter(methodDefinition -> methodDefinition.getMethod().getAnnotation(annotation) != null)
                .collect(Collectors.toList());
    }
}
