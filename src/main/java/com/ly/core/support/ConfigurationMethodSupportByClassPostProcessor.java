package com.ly.core.support;

import com.google.common.collect.Maps;
import com.ly.core.annotation.ApiAfterClass;
import com.ly.core.annotation.ApiBeforeClass;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Component;
import org.testng.ITestClass;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * ApiBeforeClass ApiAfterClass 支持
 *
 * @Author: luoy
 * @Date: 2020/12/7 15:09.
 */
@Component
public class ConfigurationMethodSupportByClassPostProcessor implements TestNgClassPostProcessor {
    private Map<String, MethodDefinitionHolder> beforeClassMethods = Maps.newConcurrentMap();

    @Override
    public void onTestClassStartBeforePostProcessor(ITestClass iTestClass) {
        Object[] instances = iTestClass.getInstances(false);
        collectBeforeClassMethod(instances);

        MethodDefinitionHolder methodDefinitionHolder = this.beforeClassMethods.get(iTestClass.getName());
        List<MethodDefinition> beforeClass = methodDefinitionHolder.byAnnotation(ApiBeforeClass.class);
        beforeClass.forEach(ConfigurationMethodSupportExcludeClassPostProcessor::runMethod);
    }

    @Override
    public void onTestClassFinishAfterPostProcessor(ITestClass iTestClass) {
        MethodDefinitionHolder methodDefinitionHolder = beforeClassMethods.get(iTestClass.getName());
        List<MethodDefinition> afterClass = methodDefinitionHolder.byAnnotation(ApiAfterClass.class);
        afterClass.forEach(ConfigurationMethodSupportExcludeClassPostProcessor::runMethod);
    }

    private void collectBeforeClassMethod(Object[] instances) {
        for (Object instance : instances) {
            List<MethodDefinition> methodDefinitions = Lists.newArrayList();
            Method[] methods = instance.getClass().getMethods();
            for (Method method : methods) {
                if (method.getAnnotation(ApiAfterClass.class) != null ||
                        method.getAnnotation(ApiBeforeClass.class) != null) {
                    Object[] args = ConfigurationMethodSupportExcludeClassPostProcessor.getArgs(method);
                    methodDefinitions.add(MethodDefinition.builder().instance(instance).method(method).args(args).build());
                }
            }
            beforeClassMethods.put(instance.getClass().getName(), MethodDefinitionHolder.builder().methodDefinitions(methodDefinitions).build());
        }
    }
}
