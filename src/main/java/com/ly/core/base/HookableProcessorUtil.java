package com.ly.core.base;

import com.ly.core.annotation.TearDown;
import com.ly.core.annotation.TestFail;
import com.ly.core.config.DefaultConstantConfig;
import com.ly.core.utils.ReflectUtil;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestResult;

import java.lang.reflect.InvocationTargetException;

/**
 * IHookable通用处理器
 * @Author: luoy
 * @Date: 2019/9/5 15:38.
 */
@Slf4j
public class HookableProcessorUtil {

    private static volatile HookableProcessorUtil INSTANCE;

    private HookableProcessorUtil() {}

    public static HookableProcessorUtil create() {
        if (INSTANCE == null) {
            synchronized (HookableProcessorUtil.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HookableProcessorUtil();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 用例失败后处理
     * @param result
     * @return
     */
    public HookableProcessorUtil testFail(ITestResult result) {
        if(isFail(result)) {
            TestFail testFail = result.getMethod().getConstructorOrMethod().getMethod().getAnnotation(TestFail.class);
            if (testFail != null) {
                ReflectUtil.hooksCall(testFail.value(), DefaultConstantConfig.TEST_ANNOTATION_HOOK_METHOD_BY_CLASS);
            }
        }
        return this;
    }

    /**
     * 用例成功后处理
     * @param result
     * @return
     */
    public HookableProcessorUtil tearDown(ITestResult result) {
        if(!isFail(result)) {
            TearDown tearDown = result.getMethod().getConstructorOrMethod().getMethod().getAnnotation(TearDown.class);
            if (tearDown != null) {
                ReflectUtil.hooksCall(tearDown.value(), DefaultConstantConfig.TEST_ANNOTATION_HOOK_METHOD_BY_CLASS);
            }
        }
        return this;
    }

    /**
     * 打印日志
     * @param testResult
     * @return
     */
    public HookableProcessorUtil logPrint(ITestResult testResult) {
        if(isFail(testResult)) {
            if (testResult.getThrowable() instanceof InvocationTargetException) {
                log.error("error", testResult.getThrowable().getCause());
            } else {
                log.error("error", testResult.getThrowable());
            }
        }
        return this;
    }

    private boolean isFail(ITestResult testResult) {
        return testResult.getThrowable() != null;
    }

}
