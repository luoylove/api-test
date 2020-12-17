package com.ly.core.parse;

import com.google.common.collect.Lists;
import com.ly.core.exception.BizException;
import org.testng.collections.Maps;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 读取yaml
 * @Author: luoy
 * @Date: 2020/4/17 15:00.
 */
public class YmlParse {
    private static Map<String, DataEntity> dataEntitys = Maps.newConcurrentMap();

    private volatile static YmlParse ymlParse = null;
    private YmlParse(){}
    public static YmlParse getInstance() {
        if (ymlParse == null) {
            synchronized (YmlParse.class) {
                if (ymlParse == null) {
                    ymlParse = new YmlParse();
                }
            }
        }
        return ymlParse;
    }

    public YmlParse load(String fileName) {
        if (dataEntitys.containsKey(fileName)) {
            return this;
        }
        Yaml yaml = new Yaml();
        String filePath = fileName;
        //先尝试加载下resource目录
        URL resource = this.getClass().getClassLoader().getResource(fileName);
        if (resource != null) {
            filePath = resource.getPath();
        }

        File yamlFile = new File(filePath);
        try {
            DataEntity dataEntity = yaml.loadAs(new FileInputStream(yamlFile), DataEntity.class);
            dataEntitys.put(fileName, dataEntity);
        } catch (FileNotFoundException e) {
            throw new BizException("文件不存在:" + filePath, e);
        }
        checkRepeatabilityByName();
        return this;
    }

    public YmlParse loads(String...paths) {
        for(String path : paths) {
            if(dataEntitys.containsKey(path)) {
                continue;
            }
            load(path);
        }
        return this;
    }

    public List<TestCase> getTestCases() {
        Collection<DataEntity> values = dataEntitys.values();
        List<TestCase> list = Lists.newArrayList();
        values.forEach(dataEntity -> list.addAll(dataEntity.getTestCase()));
        return list;
    }

    public TestCase getTestCase(String name) {
        return getTestCases().stream().filter(testCase -> testCase.getName().equals(name))
                            .findAny().orElseThrow(() ->new BizException(name + ": yml文件未找到该name"));
    }

    private void checkRepeatabilityByName() {
        List<String> names = getTestCases().stream().map(TestCase::getName).collect(Collectors.toList());
        if(names.stream().distinct().count() != names.size()) {
            Map<String, Long> counterMap = names.stream().collect(Collectors.groupingBy(name -> name, Collectors.counting()));
            List<String> distinctNames = counterMap.keySet().stream().filter(k -> counterMap.get(k) > 1).collect(Collectors.toList());
            throw new BizException("yml中有重复name,请检查: " + Arrays.asList(distinctNames));
        }
    }
}