package com.ly.core.actuator;

import org.testng.xml.XmlSuite;

import java.lang.reflect.Proxy;

/**
 * @Author: luoy
 * @Date: 2020/6/2 17:13.
 */
public class TestNgRun implements TestNgActuatorInterface{

    public  TestNgActuatorInterface proxy = (TestNgActuatorInterface) Proxy.newProxyInstance(TestNgActuator.getInstance().getClass().getClassLoader(),
                    new Class[]{TestNgActuatorInterface.class},
                    new TestNgActuatorHandler(TestNgActuator.getInstance()));

    @Override
    public  void runGroups(String name, String...groupsName) {
        proxy.runGroups(name, groupsName);
    }

    @Override
    public void runClasses(String name, String... classesName) {
        proxy.runClasses(name, classesName);
    }

    @Override
    public void runClasses4Methods(String name, String classesName, String... methodName) {
        proxy.runClasses4Methods(name, classesName, methodName);
    }

    @Override
    public void runGroups(String suiteName, String testName, XmlSuite.ParallelMode parallelMode, int threadCount, String... groupsName) {
        proxy.runGroups(suiteName, testName, parallelMode, threadCount, groupsName);
    }

    @Override
    public void runClasses(String suiteName, String testName, XmlSuite.ParallelMode parallelMode, int threadCount, String... className) {
        proxy.runClasses(suiteName, testName, parallelMode, threadCount, className);
    }

    @Override
    public void runClasses4Methods(String suiteName, String testName, XmlSuite.ParallelMode parallelMode, int threadCount, String className, String... methodName) {
        proxy.runClasses4Methods(suiteName, testName, parallelMode, threadCount, className, methodName);
    }

    @Override
    public void runFailed() {
        proxy.runFailed();
    }

    @Override
    public void runFailed(String suiteName) {
        proxy.runFailed(suiteName);
    }


    public static void main(String[] args) {
        TestNgRun run = new TestNgRun();
        run.runGroups("api-test", "api");

//        run.runFailed();
//        run.runClasses("api-test", "com.ly.testcase.ExampleApiTestCase", "com.ly.testcase.ExampleApiTestCase");
//        run.runClasses("测试", "api-test",  XmlSuite.ParallelMode.CLASSES, 2, "com.ly.testcase.ExampleApiTestCase", "com.ly.testcase.ExampleApiTestCase");
//        run.runClasses4Methods("", "com.ly.testcase.ExampleApiTestCase",  "login");
    }
}
