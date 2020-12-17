package com.ly.core.listener;

import com.ly.core.support.PostProcessorHolder;
import com.ly.core.support.TestNgLifeCyclePostProcessor;
import lombok.extern.slf4j.Slf4j;
import org.testng.IConfigurationListener;
import org.testng.IConfigurationListener2;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;


/**
 * 不建议再次拓展,建议用{@link com.ly.core.support.TestNgLifeCyclePostProcessor} 增加代码可读性
 *
 * @Author: luoy
 * @Date: 2019/9/16 11:03.
 * 整个生命周期监听器
 * <p>
 * 在 @Listeners 中添加监听器跟在 testng.xml 添加监听器一样，将被应用到整个测试套件中的测试方法。
 * 如果需要控制监听器的应用范围（比如添加的监听器仅使用于某些测试测试类或者某些测试方法），则必须在监听器类中编写适当的判断逻辑。
 * 在 @Listeners 中添加监听器跟在 testng.xml 添加监听器的不同之处在于，它不能添加 IAnnotationTransformer 和 IAnnotationTransformer2 监听器。
 * 原因是因为这两种监听器必须在更早的阶段添加到 TestNG 中才能实施修改注释的操作，所以它们只能在 testng.xml 添加。
 */
@Slf4j
public class BaseLifeCycleListener implements ITestListener, ISuiteListener, IConfigurationListener, IConfigurationListener2{
    @Override
    /**
     *
     * 测试方法执行前执行
     */
    public void onTestStart(ITestResult result) {
        PostProcessorHolder.getInstance().getPostProcessor(TestNgLifeCyclePostProcessor.class)
                .forEach(testNgLifeCyclePostProcessor -> testNgLifeCyclePostProcessor.onTestMethodStartBeforePostProcessor(result));
        log.info("=====================onTestMethodStart: {}=====================", result.getName());
    }

    @Override
    /**
     * 测试方法执行成功后执行
     */
    public void onTestSuccess(ITestResult result) {
        PostProcessorHolder.getInstance().getPostProcessor(TestNgLifeCyclePostProcessor.class)
                .forEach(testNgLifeCyclePostProcessor -> testNgLifeCyclePostProcessor.onTestMethodSuccessAfterPostProcessor(result));
        log.info("=====================onTestMethodSuccess: {}=====================", result.getName());
    }

    @Override
    /**
     * 测试方法执行失败后执行
     */
    public void onTestFailure(ITestResult result) {
        PostProcessorHolder.getInstance().getPostProcessor(TestNgLifeCyclePostProcessor.class)
                .forEach(testNgLifeCyclePostProcessor -> testNgLifeCyclePostProcessor.onTestMethodFailureAfterPostProcessor(result));
        log.info("=====================onTestMethodFailure: {}=====================", result.getName());
    }

    @Override
    /**
     * 跳过测试方法后执行
     */
    public void onTestSkipped(ITestResult result) {
        PostProcessorHolder.getInstance().getPostProcessor(TestNgLifeCyclePostProcessor.class)
                .forEach(testNgLifeCyclePostProcessor -> testNgLifeCyclePostProcessor.onTestMethodSkippedAfterPostProcessor(result));
        log.info("=====================onTestMethodSkipped: {}=====================", result.getName());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    }

    @Override
    /**
     * 在实例化测试类之后且在调用任何配置方法之前调用。
     */
    public void onStart(ITestContext context) {
        PostProcessorHolder.getInstance().getPostProcessor(TestNgLifeCyclePostProcessor.class)
                .forEach(testNgLifeCyclePostProcessor -> testNgLifeCyclePostProcessor.onTestClassInstantiationAfterPostProcessor(context));
        log.info("=====================onTestClassInstantiationSuccess=====================");
    }

    @Override
    /**
     * 在运行所有测试并调用其所有配置方法之后调用
     */
    public void onFinish(ITestContext context) {
        PostProcessorHolder.getInstance().getPostProcessor(TestNgLifeCyclePostProcessor.class)
                .forEach(testNgLifeCyclePostProcessor -> testNgLifeCyclePostProcessor.onAllTestMethodFinishAfterPostProcessor(context));
        log.info("=====================onAllTestMethodFinish=====================");
    }

    @Override
    /**
     * suite执行前执行 对应test.xml suite标签 该地方可实现IAnnotationTransformer监听器功能
     */
    public void onStart(ISuite suite) {
        PostProcessorHolder.getInstance().getPostProcessor(TestNgLifeCyclePostProcessor.class)
                .forEach(testNgLifeCyclePostProcessor -> testNgLifeCyclePostProcessor.onSuiteStartBeforePostProcessor(suite));
        log.info("=====================onSuiteStart=====================");
    }

    @Override
    /**
     * suite执行后执行 对应test.xml suite标签
     */
    public void onFinish(ISuite suite) {
        PostProcessorHolder.getInstance().getPostProcessor(TestNgLifeCyclePostProcessor.class)
                .forEach(testNgLifeCyclePostProcessor -> testNgLifeCyclePostProcessor.onSuiteFinishAfterPostProcessor(suite));
        log.info("=====================onSuiteFinish=====================");
    }

    /**
     * 配置方法执行成功时执行 (Before, after等)
     *
     * @param itr
     */
    @Override
    public void onConfigurationSuccess(ITestResult itr) {
        PostProcessorHolder.getInstance().getPostProcessor(TestNgLifeCyclePostProcessor.class)
                .forEach(testNgLifeCyclePostProcessor -> testNgLifeCyclePostProcessor.onConfigurationSuccessAfterPostProcessor(itr));
    }

    /**
     * 配置方法执行失败时执行
     *
     * @param itr
     */
    @Override
    public void onConfigurationFailure(ITestResult itr) {
        PostProcessorHolder.getInstance().getPostProcessor(TestNgLifeCyclePostProcessor.class)
                .forEach(testNgLifeCyclePostProcessor -> testNgLifeCyclePostProcessor.onConfigurationFailureAfterPostProcessor(itr));
    }

    /**
     * 配置方法跳过时执行
     *
     * @param itr
     */
    @Override
    public void onConfigurationSkip(ITestResult itr) {
        PostProcessorHolder.getInstance().getPostProcessor(TestNgLifeCyclePostProcessor.class)
                .forEach(testNgLifeCyclePostProcessor -> testNgLifeCyclePostProcessor.onConfigurationSkipAfterPostProcessor(itr));
    }

    /**
     * 配置方法运行前执行
     *
     * @param tr
     */
    @Override
    public void beforeConfiguration(ITestResult tr) {
        PostProcessorHolder.getInstance().getPostProcessor(TestNgLifeCyclePostProcessor.class)
                .forEach(testNgLifeCyclePostProcessor -> testNgLifeCyclePostProcessor.onConfigurationStartBeforePostProcessor(tr));
    }
}
