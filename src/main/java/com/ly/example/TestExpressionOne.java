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
@Story("测试one")
public class TestExpressionOne extends BaseTestCase {
    @ApiBeforeTest
    public void testTest() {
        System.out.println("===================TestExpressionOne- beforeTest =============");
    }

    @ApiAfterTest
    public void testTest1() {
        System.out.println("===================TestExpressionOne- afterTest =============");
    }



    @Severity(SeverityLevel.CRITICAL)
    @Description("thread test")
    @Test(groups = "test-groups-1", description = "test")
    public void testMethodThree() {
        String name = Thread.currentThread().getName();
        ContextDataStorage.getInstance().setThreadAttribute("testSuite", name);
        System.out.println("method testMethodThree thread id:" + name);
    }
//    @Severity(SeverityLevel.CRITICAL)
//    @Description("thread test")
//    @Test(groups = "test-groups-1", description = "test")
//    public void testMethodThree1() {
//        String name = Thread.currentThread().getName();
//        ContextDataStorage.getInstance().setThreadAttribute("testSuite", name);
//        System.out.println("method testMethodThree thread id:" + name);
//    }
//    @Severity(SeverityLevel.CRITICAL)
//    @Description("thread test")
//    @Test(groups = "test-groups-1", description = "test")
//    public void testMethodThree2() {
//        String name = Thread.currentThread().getName();
//        ContextDataStorage.getInstance().setThreadAttribute("testSuite", name);
//        System.out.println("method testMethodThree thread id:" + name);
//    }
//    @Severity(SeverityLevel.CRITICAL)
//    @Description("thread test")
//    @Test(groups = "test-groups-1", description = "test")
//    public void testMethodThree3() {
//        String name = Thread.currentThread().getName();
//        ContextDataStorage.getInstance().setThreadAttribute("testSuite", name);
//        System.out.println("method testMethodThree thread id:" + name);
//    }
//    @Severity(SeverityLevel.CRITICAL)
//    @Description("thread test")
//    @Test(groups = "test-groups-1", description = "test")
//    public void testMethodThree4() {
//        String name = Thread.currentThread().getName();
//        ContextDataStorage.getInstance().setThreadAttribute("testSuite", name);
//        System.out.println("method testMethodThree thread id:" + name);
//    }
//    @Severity(SeverityLevel.CRITICAL)
//    @Description("thread test")
//    @Test(groups = "test-groups-1", description = "test")
//    public void testMethodThree5() {
//        String name = Thread.currentThread().getName();
//        ContextDataStorage.getInstance().setThreadAttribute("testSuite", name);
//        System.out.println("method testMethodThree thread id:" + name);
//    }
}
