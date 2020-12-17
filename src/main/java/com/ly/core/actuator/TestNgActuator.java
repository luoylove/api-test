package com.ly.core.actuator;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ly.core.config.ApiClassPathScanningCandidateComponentProvider;
import com.ly.core.exception.BizException;
import com.ly.core.utils.AssertUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.ClassUtils;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlGroups;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlRun;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: luoy
 */
public class TestNgActuator implements TestNgActuatorInterface{
    public static final String DEFAULT_TESTNG_RESULT_OUTPUT = "test-output";

    public static final String DEFAULT_SUITE_NAME = "auto-test";

    public static final String DEFAULT_TEST_NAME = "api-test";

    public static final String[] DEFAULT_SCANNING_BASE_PACKAGE = {"com.ly.testcase", "com.ly.example"};

    public static final String DEFAULT_FAILED_NAME = "testng-failed.xml";

    public static final int DEFAULT_VERBOSE = 2;

    public static final XmlSuite.ParallelMode DEFAULT_PARALLEL_MODE = XmlSuite.ParallelMode.NONE;


    private static volatile TestNgActuator testNgActuator = null;

    private TestNgActuator(){}

    public static TestNgActuator getInstance() {
        if (testNgActuator == null) {
            synchronized (TestNgActuator.class) {
                if (testNgActuator == null) {
                    testNgActuator = new TestNgActuator();
                }
            }
        }
        return testNgActuator;
    }

    /**
     * 根据groups运行测试用例
     * @param name
     * @param groupsName
     */
    @Override
    public void runGroups(String name, String...groupsName) {
        runGroups("", name, null, 0, groupsName);
    }

    /**
     * 根据类运行测试用例
     * @param name
     * @param classesName
     */
    @Override
    public void runClasses(String name, String...classesName) {
        runClasses("", name, null, 0, classesName);
    }

    /**
     * 根据类运行测试用例
     * @param name
     * @param classesName
     */
    @Override
    public void runClasses4Methods(String name, String classesName, String...methodName) {
        runClasses4Methods("", name, null, 0, classesName, methodName);
    }

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
    @Override
    public void runGroups(String suiteName, String testName, XmlSuite.ParallelMode parallelMode, int threadCount, String...groupsName) {
        TestNG testNG = new TestNG();

        XmlSuite xmlSuite = createSuiteInit(suiteName, parallelMode, threadCount);
        xmlSuite.setTests(Lists.newArrayList(createTestByGroup(xmlSuite, testName, groupsName)));
        testNG.setXmlSuites(Lists.newArrayList(xmlSuite));
        testNG.run();
    }

    @Override
    public void runClasses(String suiteName, String testName, XmlSuite.ParallelMode parallelMode, int threadCount, String...className) {
        TestNG testNG = new TestNG();
        XmlSuite xmlSuite = createSuiteInit(suiteName, parallelMode, threadCount);
        xmlSuite.setTests(Lists.newArrayList(createTestByClasses(xmlSuite, testName, className)));
        testNG.setXmlSuites(Lists.newArrayList(xmlSuite));
        testNG.run();
    }

    @Override
    public void runClasses4Methods(String suiteName, String testName, XmlSuite.ParallelMode parallelMode, int threadCount, String className, String...methodName) {
        TestNG testNG = new TestNG();
        XmlSuite xmlSuite = createSuiteInit(suiteName, parallelMode, threadCount);
        xmlSuite.setTests(Lists.newArrayList(createTestByClasses4Method(xmlSuite, testName, className, methodName)));
        testNG.setXmlSuites(Lists.newArrayList(xmlSuite));
        testNG.run();
    }

    /**
     *
     */
    @Override
    public void runFailed() {
        runFailed(DEFAULT_SUITE_NAME);
    }

    @Override
    public void runFailed(String suiteName) {
        TestNG testNg = new TestNG();
        String failedPath = DEFAULT_TESTNG_RESULT_OUTPUT + File.separator + suiteName + File.separator + DEFAULT_FAILED_NAME;
        ArrayList<String> suites = Lists.newArrayList(failedPath);
        testNg.setTestSuites(suites);
        testNg.run();
    }

    /**
     * 生成Group test节点
     * @param xmlSuite
     * @param testName
     * @param groupsName
     * @return
     */
    private XmlTest createTestByGroup(XmlSuite xmlSuite, String testName, String...groupsName) {
        XmlTest xmlTest = new XmlTest(xmlSuite);
        xmlTest.setVerbose(DEFAULT_VERBOSE);
        xmlTest.setName(StringUtils.isBlank(testName) ? DEFAULT_TEST_NAME : testName);
        AssertUtils.notNull(groupsName, "testNg运行groupsName为空");
        xmlTest.setGroups(createGroups(groupsName));
        xmlTest.setClasses(createClassByAll());
        return xmlTest;
    }

    /**
     * 生成Classes test节点
     * @param xmlSuite
     * @param testName
     * @param className
     * @return
     */
    private XmlTest createTestByClasses(XmlSuite xmlSuite, String testName, String...className) {
        XmlTest xmlTest = new XmlTest(xmlSuite);
        xmlTest.setVerbose(DEFAULT_VERBOSE);
        xmlTest.setName(StringUtils.isBlank(testName) ? DEFAULT_TEST_NAME : testName);
        AssertUtils.notNull(className, "testNg运行className为空");
        List<XmlClass> xmlClasses = Arrays.stream(className).map(name -> createClass(name, null)).collect(Collectors.toList());
        xmlTest.setClasses(xmlClasses);
        return xmlTest;
    }

    /**
     * 生成method test节点
     * @param xmlSuite
     * @param testName
     * @param methodName
     * @return
     */
    private XmlTest createTestByClasses4Method(XmlSuite xmlSuite, String testName, String className, String...methodName) {
        XmlTest xmlTest = new XmlTest(xmlSuite);
        xmlTest.setVerbose(DEFAULT_VERBOSE);
        xmlTest.setName(testName);
        List<XmlClass> xmlClasses = Lists.newArrayList();
        AssertUtils.notNull(methodName, "testNg运行methodName为空");
        xmlClasses.add(createClass(className, Arrays.asList(methodName)));
        xmlTest.setClasses(xmlClasses);
        return xmlTest;
    }

    /**
     * 生成groups节点
     * @param groups
     * @return
     */
    private XmlGroups createGroups(String...groups) {
        XmlGroups xmlGroups = new XmlGroups();
        xmlGroups.setRun(createRun(null ,groups));
        return xmlGroups;
    }

    /**
     * 生成class节点
     * @param className
     * @param methodName
     * @return
     */
    private XmlClass createClass(String className, List<String> methodName) {
        XmlClass xmlClass = new XmlClass();
        xmlClass.setName(className);
        if (methodName != null && methodName.size() > 0) {
            xmlClass.setIncludedMethods(createInclude(methodName));
        }
        return xmlClass;
    }

    /**
     * 扫描所有basePackage 下面类生成class节点  (com.ly.testcase)
     * @return
     */
    private List<XmlClass> createClassByAll() {
        Set<Class<?>> testCaseClassCandidates = findTestCaseClassCandidates(DEFAULT_SCANNING_BASE_PACKAGE);
        List<XmlClass> xmlClasses = testCaseClassCandidates.stream().map(clazz -> {
            XmlClass xmlClass = new XmlClass();
            xmlClass.setClass(clazz);
            return xmlClass;
        }).collect(Collectors.toList());
        return xmlClasses;
    }

    /**
     * 创建include节点
     * @param names
     * @return
     */
    private List<XmlInclude> createInclude(List<String> names) {
        return names.stream().map(name -> {
            XmlInclude xmlInclude = new XmlInclude(name);
            return xmlInclude;
        }).collect(Collectors.toList());
    }

    /**
     * 创建run节点
     * @param excludes
     * @param includes
     * @return
     */
    private XmlRun createRun(String[] excludes, String[] includes) {
        XmlRun xmlRun = new XmlRun();
        if (includes != null) {
            for(String name : includes) {
                xmlRun.onInclude(name);
            }
        }

        if (excludes != null) {
            for(String name : excludes) {
                xmlRun.onExclude(name);
            }
        }
        return xmlRun;
    }

    /**
     * suite初始化, 设置并发
     * @param suiteName
     * @param parallelMode
     * @param threadCount
     * @return
     */
    private XmlSuite createSuiteInit(String suiteName, XmlSuite.ParallelMode parallelMode, int threadCount) {
        XmlSuite xmlSuite = new XmlSuite();
        xmlSuite.setParallel(parallelMode == null ? DEFAULT_PARALLEL_MODE : parallelMode);
        xmlSuite.setThreadCount(threadCount <= 0 ? 0 : threadCount);
        xmlSuite.setName(StringUtils.isBlank(suiteName) ? DEFAULT_SUITE_NAME : suiteName);
        return xmlSuite;
    }


    /**
     * 扫描包下所有类
     * @param basePackages
     * @return
     */
    private Set<Class<?>> findTestCaseClassCandidates(String...basePackages) {
        Set<Class<?>> candidates = Sets.newLinkedHashSet();
        for(String basePackage : basePackages) {
            String convertPath = ClassUtils.convertClassNameToResourcePath(new StandardEnvironment().resolveRequiredPlaceholders(basePackage));
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    convertPath + '/' + ApiClassPathScanningCandidateComponentProvider.DEFAULT_RESOURCE_PATTERN;
            try {
                Resource[] resources = new PathMatchingResourcePatternResolver().getResources(packageSearchPath);
                for (Resource resource : resources) {
                    if (resource.isReadable()) {
                        MetadataReader metadataReader = new CachingMetadataReaderFactory().getMetadataReader(resource);
                        if (isTestCase(metadataReader)) {
                            String clazzName = metadataReader.getClassMetadata().getClassName();
                            Class<?> clazz = Class.forName(clazzName);
                            candidates.add(clazz);
                        }
                    }
                }
            } catch (IOException e) {
                throw  new BizException("I/O failure during classpath scanning", e);
            } catch (ClassNotFoundException e) {
                throw  new BizException("类未找到:", e);
            }
        }
        return candidates;
    }

    private boolean isTestCase(MetadataReader metadataReader){
        return !metadataReader.getClassMetadata().isAbstract() && !metadataReader.getClassMetadata().isInterface()
                && !metadataReader.getClassMetadata().isAnnotation() && metadataReader.getClassMetadata().isConcrete()
                && !metadataReader.getClassMetadata().isFinal() && metadataReader.getClassMetadata().isIndependent()
                && metadataReader.getClassMetadata().hasSuperClass();
    }

}
