package com.ly.core.support;

import org.testng.ITestClass;

/**
 * http请求后置处理器
 * @Author: luoy
 * @Date: 2020/12/1 18:08.
 */
public interface TestNgClassPostProcessor extends PostProcessor{
    /**
     * 测试类运行前执行
     * @param iTestClass
     */
    void onTestClassStartBeforePostProcessor(ITestClass iTestClass);

    /**
     * 测试类运行后执行
     * @param iTestClass
     */
    void onTestClassFinishAfterPostProcessor(ITestClass iTestClass);


}
