package com.ly.core.listener;

import com.ly.core.annotation.RetryCount;
import com.ly.core.utils.ResourcesUtil;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * @Author: luoy
 * @Date: 2019/8/7 16:47.
 */
public class RetryAnalyzer implements IRetryAnalyzer {
    private int counter = 0;
    /**
     * 如果要重新执行失败的测试，此方法实现应返回true，如果不想重新执行测试，则返回false
     * @param iTestResult
     * @return
     */
    @Override
    public boolean retry(ITestResult iTestResult) {
        int retryLimit = 0;

        RetryCount retryMethodCount = iTestResult.getMethod().getMethod().getAnnotation(RetryCount.class);
        //方法上没有@RetryCount注解取类上@RetryCount注解
        if (retryMethodCount == null) {
            RetryCount typeRetry = iTestResult.getTestClass().getRealClass().getAnnotation(RetryCount.class);
            //类上没有@RetryCount注解取配置文件
            if (typeRetry == null) {
                boolean retryable = ResourcesUtil.getPropBoolean("retry.retryable");
                if(retryable) {
                    retryLimit = ResourcesUtil.getPropInt("retry.count");
                }
            } else {
                retryLimit = typeRetry.count();
            }
        } else {
            retryLimit = retryMethodCount.count();
        }

        if(counter < retryLimit)
        {
            counter++;
            return true;
        }
        return false;
    }
}
