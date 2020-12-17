package com.ly.example;

import com.ly.core.annotation.ApiAfterTest;
import com.ly.core.annotation.ApiBeforeTest;
import com.ly.core.base.BaseTestCase;
import com.ly.core.utils.ContextDataStorage;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

/**
 * @Author: luoy
 * @Date: 2019/8/7 15:17.
 */
@Feature("ces")
@Story("测试two")
public class TestExpressionTwo extends BaseTestCase {

    @ApiBeforeTest
    public void testTest() {
        System.out.println("===================TestExpressionTwo- beforeTest =============");
    }

    @ApiAfterTest
    public void testTest1() {
        System.out.println("===================TestExpressionTwo- afterTest =============");
    }


    @Severity(SeverityLevel.CRITICAL)
    @Description("thread test")
    @Test(groups = "test-groups-2")
    public void testMethodOne() {
        String name = Thread.currentThread().getName();
        System.out.println("method testMethodOne thread id:" + name);
    }

    @Severity(SeverityLevel.CRITICAL)
    @Description("thread test")
    @Test(groups = "test-groups-1")
    public void testMethodTwo() {
        String name = Thread.currentThread().getName();
        ContextDataStorage.getInstance().setThreadAttribute("testSuite", name);
        System.out.println("method testMethodTwo thread id:" + name);
    }
//    @Severity(SeverityLevel.CRITICAL)
//    @Description("thread test")
//    @Test(groups = "test-groups-1")
//    public void testMethodTwo1() {
//        String name = Thread.currentThread().getName();
//        ContextDataStorage.getInstance().setThreadAttribute("testSuite", name);
//        System.out.println("method testMethodTwo thread id:" + name);
//    }
//    @Severity(SeverityLevel.CRITICAL)
//    @Description("thread test")
//    @Test(groups = "test-groups-1")
//    public void testMethodTwo2() {
//        String name = Thread.currentThread().getName();
//        ContextDataStorage.getInstance().setThreadAttribute("testSuite", name);
//        System.out.println("method testMethodTwo thread id:" + name);
//    }
//    @Severity(SeverityLevel.CRITICAL)
//    @Description("thread test")
//    @Test(groups = "test-groups-1")
//    public void testMethodTwo3() {
//        String name = Thread.currentThread().getName();
//        ContextDataStorage.getInstance().setThreadAttribute("testSuite", name);
//        System.out.println("method testMethodTwo thread id:" + name);
//    }
//    @Severity(SeverityLevel.CRITICAL)
//    @Description("thread test")
//    @Test(groups = "test-groups-1")
//    public void testMethodTwo4() {
//        String name = Thread.currentThread().getName();
//        ContextDataStorage.getInstance().setThreadAttribute("testSuite", name);
//        System.out.println("method testMethodTwo thread id:" + name);
//    }
//    @Severity(SeverityLevel.CRITICAL)
//    @Description("thread test")
//    @Test(groups = "test-groups-1")
//    public void testMethodTwo5() {
//        String name = Thread.currentThread().getName();
//        ContextDataStorage.getInstance().setThreadAttribute("testSuite", name);
//        System.out.println("method testMethodTwo thread id:" + name);
//    }
//    @Severity(SeverityLevel.CRITICAL)
//    @Description("thread test")
//    @Test(groups = "test-groups-1")
//    public void testMethodTwo6() {
//        String name = Thread.currentThread().getName();
//        ContextDataStorage.getInstance().setThreadAttribute("testSuite", name);
//        System.out.println("method testMethodTwo thread id:" + name);
//    }
//    @Severity(SeverityLevel.CRITICAL)
//    @Description("thread test")
//    @Test(groups = "test-groups-1")
//    public void testMethodTwo7() {
//        String name = Thread.currentThread().getName();
//        ContextDataStorage.getInstance().setThreadAttribute("testSuite", name);
//        System.out.println("method testMethodTwo thread id:" + name);
//    }
}
