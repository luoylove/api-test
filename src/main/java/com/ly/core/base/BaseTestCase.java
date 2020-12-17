package com.ly.core.base;

import com.ly.core.listener.BaseLifeCycleListener;
import com.ly.core.listener.TestClassListener;
import com.ly.core.parse.BaseModel;
import com.ly.core.parse.MultipleModel;
import com.ly.core.utils.ContextDataStorage;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.IHookCallBack;
import org.testng.ITestResult;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;

import java.lang.reflect.Method;

/**
 *
 * @author luoy
 * @date 2019/7/12
 */
//properties属性指定本地测试需要用到的配置文件
//本地运行时，需要设置properties值为具体的某个配置文件
@SpringBootTest(properties = {"spring.profiles.active=qa"})
//通过maven命令运行时需要把该参数去掉
//@SpringBootTest
@Listeners({BaseLifeCycleListener.class, TestClassListener.class})
public abstract class BaseTestCase extends AbstractTestNGSpringContextTests {
    public static final String PARAMETER_KEY = "&&PARAMETER_KEY";
    /**
     * 数据驱动，frameworkMethod运行的测试类
     * @param method
     * @return
     */
    @DataProvider
    public static Object[][] loadDataFile(Method method){
        return DataProviderUtils.loadDataFile(method);
    }

    /**
     * 数据驱动
     * @param method
     * @return
     */
    @DataProvider
    public static Object[][] loadDataParams(Method method){
        return DataProviderUtils.loadDataParams(method);
    }

    /**
     * 数据驱动
     * @param method
     * @return
     */
    @DataProvider
    public static Object[][] loadDataModel(Method method) {
        return DataProviderUtils.loadDataModel(method);
    }

    @Override
    protected void springTestContextPrepareTestInstance() throws Exception {
        super.springTestContextPrepareTestInstance();
    }

    /**
     * 把spring容器启动设置为测试类实例化时候
     */
    public BaseTestCase() {
        super();
        try {
            springTestContextPrepareTestInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * IHookable 监听器提供了类似与面向方面编程（AOP）中的 Around Advice 的功能。
     * 它在测试方法执行前后提供了切入点，从而使用户能够在测试方法运行前后注入特定的功能。
     * 例如，用户可以在当前测试方法运行前加入特定的验证逻辑以决定测试方法是否运行或者跳过，甚至覆盖测试方法的逻辑。
     * 下面是 IHookable 监听器要求实现的方法签名。
     *
     * void run(IHookCallBack callBack, ITestResult testResult)
     * 如要运行原始测试方法逻辑，需要调用 runTestMethod 方法。
     *
     * callBack.runTestMethod(testResult);
     * @param callBack
     * @param testResult
     */
    @Override
    public void run(IHookCallBack callBack, ITestResult testResult) {
        //方法请求参数保存下
        Object[] parameters = callBack.getParameters();
        if(parameters.length == 1) {
            if(parameters[0] instanceof BaseModel || parameters[0] instanceof MultipleModel) {
                ContextDataStorage.getInstance().setMethodAttribute(BaseTestCase.PARAMETER_KEY, parameters[0]);
            }
        }
        callBack.runTestMethod(testResult);
    }

    /**
     * Features:标注主要功能模块
     *      主要通过模块base类来标注，该模块下所有都继承该base类
     * Stories:标注Features功能模块下的分支功能
     * Title:标注Stories下测试用例名称
     * Step:标注测试用例的重要步骤
     * Severity:标注测试用例的重要级别
     * Description: 标注测试用例的描述
     * Issue和TestCaseId据说是可以集成bug管理系统的
     *
     * 1. Blocker级别——中断缺陷
     *     客户端程序无响应，无法执行下一步操作。
     * 2. Critical级别――临界缺陷，包括：
     *     功能点缺失，客户端爆页。
     * 3. Major级别——较严重缺陷，包括：
     *     功能点没有满足需求。
     * 4. Normal级别――普通缺陷，包括：
     *     1. 数值计算错误
     *     2. JavaScript错误。
     * 5. Minor级别———次要缺陷，包括：
     *     1. 界面错误与UI需求不符。
     *     2. 打印内容、格式错误
     *     3. 程序不健壮，操作未给出明确提示。
     * 6. Trivial级别——轻微缺陷，包括：
     *     1. 辅助说明描述不清楚
     *     2. 显示格式不规范，数字，日期等格式。
     *     3. 长时间操作未给用户进度提示
     *     4. 提示窗口文字未采用行业术语
     *     5. 可输入区域和只读区域没有明显的区分标志
     *     6. 必输项无提示，或者提示不规范。
     * 7. Enhancement级别——测试建议、其他（非缺陷）
     *    1. 以客户角度的易用性测试建议。
     *    2. 通过测试挖掘出来的潜在需求。
     */
    public void intor() {

    }
}
