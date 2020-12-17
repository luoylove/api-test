package com.ly.core.support;

import com.ly.core.utils.ContextDataStorage;
import org.springframework.stereotype.Component;
import org.testng.ITestClass;

/**
 * class级别线程安全的缓存处理
 * @Author: luoy
 * @Date: 2020/12/8 16:16.
 */
@Component
public class CacheLifeCycleByClassPostProcessor implements TestNgClassPostProcessor{
    @Override
    public void onTestClassStartBeforePostProcessor(ITestClass iTestClass) {
    }

    @Override
    public void onTestClassFinishAfterPostProcessor(ITestClass iTestClass) {
        //class缓存清理
        ContextDataStorage.getInstance().removeAllClassAttribute();
    }
}
