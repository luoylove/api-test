package com.ly.example;

import com.ly.core.annotation.RetryCount;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @Author: luoy
 * @Date: 2019/8/7 17:14.
 */
@Feature("ces")
@Story("测试retry")
@RetryCount(count = 4)
public class TestExpressionRetry {

    @Severity(SeverityLevel.CRITICAL)
    @Description("thread test")
    @Test(groups = "test-groups-1")
    @RetryCount(count = 2)
    public void testRetryOne() {
        long id = Thread.currentThread().getId();
        System.out.println("method testMethodThree thread id:" + id);
        Assert.assertNotNull(null);
    }

    @Severity(SeverityLevel.CRITICAL)
    @Description("thread test")
    @Test(groups = "test-groups-2")
    public void testRetryTwo() {
        long id = Thread.currentThread().getId();
        System.out.println("method testMethodThree thread id:" + id);
        Assert.assertNotNull(null);
    }
}
