package com.ly.core.support;

import com.ly.core.annotation.DataFile;
import com.ly.core.annotation.DataModel;
import com.ly.core.annotation.DataParams;
import com.ly.core.listener.RetryAnalyzer;
import com.ly.core.utils.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.testng.ISuite;
import org.testng.ITestNGMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * 在加载suite之前对@Test注解的一些操作
 * @Author: luoy
 * @Date: 2020/12/2 17:39.
 */
@Component
public class TestAnnotatinPostProcessor extends TestNgLifeCyclePostProcessorAdapter {
    @Override
    public void onSuiteStartBeforePostProcessor(ISuite suite){
        List<ITestNGMethod> allMethods = suite.getAllMethods();
        allMethods.forEach(method -> {
            Test annotation = method.getConstructorOrMethod().getMethod().getAnnotation(Test.class);
            //获取运行的testNg方法
            //获取方法上的Test注解
            if (annotation != null) {
                Class<?> retryAnalyzer = annotation.retryAnalyzer();
                //设置重试
                if (retryAnalyzer == null) {
                    AnnotationUtils.updateAnnotationValue(annotation, "retryAnalyzer", RetryAnalyzer.class);
                }

                //注解为@DataParams, 设置@Test的dataProvider为loadDataParams
                DataParams dataParams = method.getConstructorOrMethod().getMethod().getAnnotation(DataParams.class);
                if (dataParams != null) {
                    AnnotationUtils.updateAnnotationValue(annotation, "dataProvider", "loadDataParams");
                }

                //注解为@DataFile, 设置@Test的dataProvider为loadDataFile
                DataFile dataFile = method.getConstructorOrMethod().getMethod().getAnnotation(DataFile.class);
                if (dataFile != null) {
                    AnnotationUtils.updateAnnotationValue(annotation, "dataProvider", "loadDataFile");
                }

                //注解为@DataModel, 设置@Test的dataProvider为loadDataModel
                DataModel dataModel = method.getConstructorOrMethod().getMethod().getAnnotation(DataModel.class);
                if (dataModel != null) {
                    AnnotationUtils.updateAnnotationValue(annotation, "dataProvider", "loadDataModel");
                }
            }
        });
    }
}
