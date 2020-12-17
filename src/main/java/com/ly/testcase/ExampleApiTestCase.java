package com.ly.testcase;

import com.ly.api.BaseTestApiTestCase;
import com.ly.core.annotation.ApiAfterClass;
import com.ly.core.annotation.ApiAfterMethod;
import com.ly.core.annotation.ApiAfterSuite;
import com.ly.core.annotation.ApiBeforeClass;
import com.ly.core.annotation.ApiBeforeMethod;
import com.ly.core.annotation.ApiBeforeSuite;
import com.ly.core.annotation.DataModel;
import com.ly.core.parse.BaseModel;
import com.ly.core.parse.MultipleModel;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

/**
 * @Author: luoy
 * @Date: 2019/8/22 10:17.
 */
@Story("登录模块接口")
public class ExampleApiTestCase extends BaseTestApiTestCase {

    @DataModel(value = {"login-not-found"},
            format = DataModel.Format.SINGLE,
            path = {"example.yml"})
    @ApiBeforeClass
    public void beforeClass(BaseModel model) {
        System.out.println("===========beforeClass============: " + model);
    }

    @DataModel(value = {"login-not-found"},
            format = DataModel.Format.SINGLE,
            path = {"example.yml"})
    @ApiBeforeMethod
    public void beforeMethod(BaseModel model) {
        System.out.println("===========beforeMethod============: " + model);
    }

    @DataModel(value = {"login-not-found"},
            format = DataModel.Format.SINGLE,
            path = {"example.yml"})
    @ApiBeforeSuite
    public void beforeSuite(BaseModel model) {
        System.out.println("=============beforeSuite==========" + model);
    }

    @DataModel(value = {"login-not-found"},
            format = DataModel.Format.SINGLE,
            path = {"example.yml"})
    @ApiAfterSuite
    public void afterSuite(BaseModel model) {
        System.out.println("=============afterSuite==========" + model);
    }

    @DataModel(value = {"login-not-found"},
            format = DataModel.Format.SINGLE,
            path = {"example.yml"})
    @ApiAfterClass
    public void afterClass(BaseModel model) {
        System.out.println("=============afterClass==========" + model);
    }

    @DataModel(value = {"login-not-found"},
            format = DataModel.Format.SINGLE,
            path = {"example.yml"})
    @ApiAfterMethod
    public void afterMethod(BaseModel model) {
        System.out.println("=============afterMethod==========" + model);
    }



    @Severity(SeverityLevel.CRITICAL)
    @Description("登录")
    @Test(groups = "example")
    @DataModel(value = {"login-not-found"},
            format = DataModel.Format.SINGLE,
            path = {"example.yml"})
    public void login(BaseModel model) {
        testApiClient.doHttp(model).auto();
    }

    @Severity(SeverityLevel.CRITICAL)
    @Description("获取司机信息")
    @Test(groups = "example")
    @DataModel(format = DataModel.Format.MULTIPLE, path = "example.yml")
    public void test(MultipleModel model) {
        testApiClient.doHttp("login-not-found").auto();

        testApiClient.doHttp("GetSeriesGames").auto();
    }
}
