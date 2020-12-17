package com.ly.core.actuator;

import org.testng.xml.XmlSuite;

/**
 * @Author: luoy
 */
public interface TestNgActuatorInterface {

    /**
     * 根据groups运行测试用例
     * @param name
     * @param groupsName
     */
    void runGroups(String name, String...groupsName);

    /**
     * 根据类运行测试用例
     * @param name
     * @param classesName
     */
    void runClasses(String name, String...classesName);

    /**
     * 根据类中方法运行测试用例
     * @param name
     * @param classesName
     */
    void runClasses4Methods(String name, String classesName, String...methodName);

    /**
     * 指定groups运行
     * @param suiteName
     * @param testName
     * @param parallelMode
     *              并发级别
     * @param threadCount
     *              并发线程数
     * @param groupsName
     *              groups名字
     */
    void runGroups(String suiteName, String testName, XmlSuite.ParallelMode parallelMode, int threadCount, String...groupsName);

    /**
     * 指定类运行
     * @param suiteName
     * @param testName
     * @param parallelMode
     * @param threadCount
     * @param className
     */
    void runClasses(String suiteName, String testName, XmlSuite.ParallelMode parallelMode, int threadCount, String...className);

    /**
     * 指定类与类中方法运行
     * @param suiteName
     * @param testName
     * @param parallelMode
     * @param threadCount
     * @param className
     * @param methodName
     */
    void runClasses4Methods(String suiteName, String testName, XmlSuite.ParallelMode parallelMode, int threadCount, String className, String...methodName);

    /**
     * 失败重跑 默认重跑的suitename为TestNgActuator.DEFAULT_SUITE_NAME
     */
    void runFailed();

    /**
     * 失败重跑
     * @param suiteName
     */
    void runFailed(String suiteName);
}
