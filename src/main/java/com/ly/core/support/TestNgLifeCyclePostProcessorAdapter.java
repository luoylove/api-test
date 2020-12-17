package com.ly.core.support;

import org.testng.ISuite;
import org.testng.ITestContext;
import org.testng.ITestResult;

/**
 * 适配器
 * @Author: luoy
 * @Date: 2020/12/2 17:12.
 */
public abstract class TestNgLifeCyclePostProcessorAdapter implements TestNgLifeCyclePostProcessor{
    @Override
    public void onTestMethodStartBeforePostProcessor(ITestResult result) {

    }

    @Override
    public void onTestMethodSuccessAfterPostProcessor(ITestResult result) {

    }

    @Override
    public void onTestMethodFailureAfterPostProcessor(ITestResult result) {

    }

    @Override
    public void onTestMethodSkippedAfterPostProcessor(ITestResult result) {

    }

    @Override
    public void onTestClassInstantiationAfterPostProcessor(ITestContext context) {

    }

    @Override
    public void onAllTestMethodFinishAfterPostProcessor(ITestContext context) {

    }

    @Override
    public void onSuiteStartBeforePostProcessor(ISuite suite) {

    }

    @Override
    public void onSuiteFinishAfterPostProcessor(ISuite suite) {

    }

    @Override
    public void onConfigurationStartBeforePostProcessor(ITestResult result) {

    }

    @Override
    public void onConfigurationSuccessAfterPostProcessor(ITestResult result) {

    }

    @Override
    public void onConfigurationFailureAfterPostProcessor(ITestResult result) {

    }

    @Override
    public void onConfigurationSkipAfterPostProcessor(ITestResult result) {

    }
}
