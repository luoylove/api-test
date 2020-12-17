package com.ly.core.support;

import org.testng.ISuite;
import org.testng.ITestContext;
import org.testng.ITestResult;

/**
 * http请求后置处理器
 * @Author: luoy
 * @Date: 2020/12/1 18:08.
 */
public interface TestNgLifeCyclePostProcessor extends PostProcessor{

    /**
     * 测试方法执行前执行
     * @param result
     */
    void onTestMethodStartBeforePostProcessor(ITestResult result);

    /**
     * 测试方法执行成功后执行
     * @param result
     */
    void onTestMethodSuccessAfterPostProcessor(ITestResult result);


    /**
     * 测试方法执行失败后执行
     * @param result
     */
    void onTestMethodFailureAfterPostProcessor(ITestResult result);

    /**
     * 跳过测试方法后执行
     * @param result
     */
    void onTestMethodSkippedAfterPostProcessor(ITestResult result);

    /**
     * 在实例化测试类之后且在调用任何配置方法之前调用
     * @param context
     */
    void onTestClassInstantiationAfterPostProcessor(ITestContext context);

    /**
     * 在运行所有测试并调用其所有配置方法之后调用
     * @param context
     */
    void onAllTestMethodFinishAfterPostProcessor(ITestContext context);

    /**
     * suite执行之前执行 对应test.xml suite标签
     * @param suite
     */
    void onSuiteStartBeforePostProcessor(ISuite suite);

    /**
     * suite执行后执行 对应test.xml suite标签
     * @param suite
     */
    void onSuiteFinishAfterPostProcessor(ISuite suite);

    /**
     * 配置方法运行前执行(配置方法: beforeTest,AfterTest等)
     * @param result
     */
    void onConfigurationStartBeforePostProcessor(ITestResult result);

    /**
     * 配置方法执行成功时执行
     * @param result
     */
    void onConfigurationSuccessAfterPostProcessor(ITestResult result);

    /**
     * 配置方法执行失败时执行
     * @param result
     */
    void onConfigurationFailureAfterPostProcessor(ITestResult result);

    /**
     * 配置方法跳过时执行
     * @param result
     */
    void onConfigurationSkipAfterPostProcessor(ITestResult result);
}
