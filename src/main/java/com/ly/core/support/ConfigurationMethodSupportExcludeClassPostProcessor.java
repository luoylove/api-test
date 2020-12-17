package com.ly.core.support;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ly.core.annotation.ApiAfterMethod;
import com.ly.core.annotation.ApiAfterSuite;
import com.ly.core.annotation.ApiAfterTest;
import com.ly.core.annotation.ApiBeforeMethod;
import com.ly.core.annotation.ApiBeforeSuite;
import com.ly.core.annotation.ApiBeforeTest;
import com.ly.core.annotation.DataFile;
import com.ly.core.annotation.DataModel;
import com.ly.core.annotation.DataParams;
import com.ly.core.base.BaseTestCase;
import com.ly.core.exception.BizException;
import com.ly.core.parse.BaseModel;
import com.ly.core.parse.MultipleModel;
import com.ly.core.utils.ContextDataStorage;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Component;
import org.testng.ISuite;
import org.testng.ITestClass;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * configuration类型方法支持 (不包括BeforeClass)
 *
 * @Author: luoy
 * @Date: 2020/12/2 17:39.
 */
@Component
public class ConfigurationMethodSupportExcludeClassPostProcessor extends TestNgLifeCyclePostProcessorAdapter {
    private Map<String, MethodDefinitionHolder> configurationMethods = Maps.newConcurrentMap();

    @Override
    public void onSuiteStartBeforePostProcessor(ISuite suite) {
        Set<Object> instances = Sets.newHashSet();
        suite.getAllMethods().forEach(consumer -> {
            Object instance = consumer.getInstance();
            instances.add(instance);
        });
        collectConfigurationMethod(instances);
        configurationMethods.values().forEach(methodDefinitionHolder -> {
            List<MethodDefinition> beforeSuite = methodDefinitionHolder.byAnnotation(ApiBeforeSuite.class);
            //beforeSuite run
            beforeSuite.forEach(ConfigurationMethodSupportExcludeClassPostProcessor::runMethod);
        });
    }

    @Override
    public void onTestClassInstantiationAfterPostProcessor(ITestContext context) {
        Arrays.stream(context.getAllTestMethods())
                .map(ITestNGMethod::getTestClass)
                .map(ITestClass::getName)
                .forEach( testClassName -> {
                    MethodDefinitionHolder methodDefinitionHolder = configurationMethods.get(testClassName);
                    List<MethodDefinition> beforeTest = methodDefinitionHolder.byAnnotation(ApiBeforeTest.class);
                    beforeTest.forEach(ConfigurationMethodSupportExcludeClassPostProcessor::runMethod);
                });
    }

    @Override
    public void onAllTestMethodFinishAfterPostProcessor(ITestContext context) {
        Arrays.stream(context.getAllTestMethods())
                .map(ITestNGMethod::getTestClass)
                .map(ITestClass::getName)
                .forEach( testClassName -> {
                    MethodDefinitionHolder methodDefinitionHolder = configurationMethods.get(testClassName);
                    List<MethodDefinition> afterMethod = methodDefinitionHolder.byAnnotation(ApiAfterTest.class);
                    afterMethod.forEach(ConfigurationMethodSupportExcludeClassPostProcessor::runMethod);
                });
    }

    @Override
    public void onTestMethodStartBeforePostProcessor(ITestResult result) {
        MethodDefinitionHolder methodDefinitionHolder = configurationMethods.get(result.getTestClass().getName());
        List<MethodDefinition> beforeMethod = methodDefinitionHolder.byAnnotation(ApiBeforeMethod.class);
        beforeMethod.forEach(ConfigurationMethodSupportExcludeClassPostProcessor::runMethod);
    }

    @Override
    public void onTestMethodSuccessAfterPostProcessor(ITestResult result) {
        onTestMethodFinishAfterPostProcessor(result);
    }

    @Override
    public void onTestMethodFailureAfterPostProcessor(ITestResult result) {
        onTestMethodFinishAfterPostProcessor(result);
    }

    @Override
    public void onTestMethodSkippedAfterPostProcessor(ITestResult result) {
        onTestMethodFinishAfterPostProcessor(result);
    }

    @Override
    public void onSuiteFinishAfterPostProcessor(ISuite suite) {
        configurationMethods.values().forEach(methodDefinitionHolder -> {
            List<MethodDefinition> afterSuite = methodDefinitionHolder.byAnnotation(ApiAfterSuite.class);
            //afterSuite run
            afterSuite.forEach(ConfigurationMethodSupportExcludeClassPostProcessor::runMethod);
        });
    }

    private void collectConfigurationMethod(Collection<Object> instances) {
        for (Object instance : instances) {
            List<MethodDefinition> methodDefinitions = Lists.newArrayList();
            Method[] methods = instance.getClass().getMethods();
            for (Method method : methods) {
                if (isConfiguration(method)) {
                    Object[] args = getArgs(method);
                    methodDefinitions.add(MethodDefinition.builder().instance(instance).method(method).args(args).build());
                }
            }
            configurationMethods.put(instance.getClass().getName(), MethodDefinitionHolder.builder().methodDefinitions(methodDefinitions).build());
        }
    }

    private boolean isConfiguration(Method method) {
        ApiBeforeMethod beforeMethod = method.getAnnotation(ApiBeforeMethod.class);
        if (beforeMethod != null) {
            String[] methods = beforeMethod.excludeOnMethod();
            return !Arrays.asList(methods).contains(method.getName());
        }

        ApiAfterMethod afterMethod = method.getAnnotation(ApiAfterMethod.class);
        if (afterMethod != null) {
            String[] methods = afterMethod.excludeOnMethod();
            return !Arrays.asList(methods).contains(method.getName());
        }

        return method.getAnnotation(ApiAfterSuite.class) != null ||
                method.getAnnotation(ApiAfterTest.class) != null ||
                method.getAnnotation(ApiBeforeSuite.class) != null ||
                method.getAnnotation(ApiBeforeTest.class) != null;
    }

    /**
     * 取configuration method args, 如果有parameters 只取第一个
     */
    protected static Object[] getArgs(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 0) {
            return null;
        }
        DataModel dataModel = method.getAnnotation(DataModel.class);
        if (Objects.nonNull(dataModel)) {
            return BaseTestCase.loadDataModel(method)[0];
        }
        DataParams dataParams = method.getAnnotation(DataParams.class);
        if (Objects.nonNull(dataParams)) {
            return BaseTestCase.loadDataParams(method)[0];
        }
        DataFile dataFile = method.getAnnotation(DataFile.class);
        if (Objects.nonNull(dataFile)) {
            return BaseTestCase.loadDataFile(method)[0];
        }

        throw new BizException("method: %s, 参数类型不匹配,请检查", method.getName());
    }

    protected static void runMethod(MethodDefinition methodDefinition) {
        try {
            saveArgs(methodDefinition.getArgs());
            methodDefinition.getMethod().invoke(methodDefinition.getInstance(), methodDefinition.getArgs());
            clearConfigurationMethodStorage();
        } catch (IllegalAccessException e) {
            throw new BizException(e);
        } catch (InvocationTargetException e) {
            throw new BizException(e.getTargetException());
        } catch (IllegalArgumentException e) {
            throw new BizException("method:" + methodDefinition.getMethod().getName() + ", 参数不匹配", e);
        }
    }

    private void onTestMethodFinishAfterPostProcessor(ITestResult result) {
        MethodDefinitionHolder methodDefinitionHolder = configurationMethods.get(result.getTestClass().getName());
        List<MethodDefinition> afterMethod = methodDefinitionHolder.byAnnotation(ApiAfterMethod.class);
        afterMethod.forEach(ConfigurationMethodSupportExcludeClassPostProcessor::runMethod);
    }

    /**
     * 请求参数保存,用于支持 {@link com.ly.api.BaseHttpClient  doHttp(String modelName)}
     *
     * @param args
     */
    private static void saveArgs(Object[] args) {
        if (args == null || args.length != 1) {
            return;
        }
        if (args[0] instanceof BaseModel || args[0] instanceof MultipleModel) {
            ContextDataStorage.getInstance().setMethodAttribute(BaseTestCase.PARAMETER_KEY, args[0]);
        }
    }

    private static void clearConfigurationMethodStorage() {
        ContextDataStorage.getInstance().removeAllMethodAttribute();
    }
}
