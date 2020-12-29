package com.ly.core.base;

import com.google.common.collect.Lists;
import com.ly.core.annotation.DataFile;
import com.ly.core.annotation.DataModel;
import com.ly.core.annotation.DataParams;
import com.ly.core.enums.ModelType;
import com.ly.core.exception.BizException;
import com.ly.core.parse.BaseModel;
import com.ly.core.parse.CsvParse;
import com.ly.core.parse.FormModel;
import com.ly.core.parse.JsonModel;
import com.ly.core.parse.MultipleModel;
import com.ly.core.parse.TestCase;
import com.ly.core.parse.TextModel;
import com.ly.core.parse.XmlModel;
import com.ly.core.parse.YmlParse;
import com.ly.core.utils.AssertUtils;
import com.ly.core.utils.JSONSerializerUtil;
import com.ly.core.utils.PatternUtil;
import com.ly.core.utils.TestCase2BaseModel;
import com.ly.core.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.testng.collections.Maps;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author: luoy
 * @Date: 2020/6/4 16:51.
 */
public class DataProviderUtils {
    /**
     * 数据驱动，frameworkMethod运行的测试类
     *
     * @param method
     * @return
     */
    public static Object[][] loadDataFile(Method method) {
        DataFile datafile = method.getAnnotation(DataFile.class);

        AssertUtils.notNull(datafile, method.getName() + "方法添加了DataProvider数据驱动，没有添加@DataFile注解");

        String path = datafile.path();
        DataFile.Format type = method.getAnnotation(DataFile.class).format();
        if (type == DataFile.Format.CSV) {
            return CsvParse.getParseFromCsv(path);
        }
        return null;
    }

    public static Object[][] loadDataParams(Method method) {
        DataParams dataParams = method.getAnnotation(DataParams.class);

        AssertUtils.notNull(dataParams, method.getName() + "方法添加了DataProvider数据驱动，没有添加@DataParams注解");

        String[] values = dataParams.value();

        String split = dataParams.splitBy();

        List<Object[]> result = Lists.newArrayList();

        for (int i = 0; i < values.length; i++) {
            String[] v1 = values[i].split(split, -1);
            result.add(v1);
        }

        return wildcardMatcher(Utils.listToArray(result));
    }

    public static Object[][] loadDataModel(Method method) {
        DataModel dataModel = method.getAnnotation(DataModel.class);
        AssertUtils.notNull(dataModel, method.getName() + "方法添加了DataProvider数据驱动，没有添加@DataModel注解");
        YmlParse ymlParse = YmlParse.getInstance().loads(dataModel.path());

        //如果是single单接口, value必填, 如果是MULTIPLE多接口, 全部引入
        if (dataModel.format() == DataModel.Format.SINGLE) {
            AssertUtils.expression(dataModel.value().length <= 0, "@DataModel中未选数据");
            List<TestCase> testCasesByParameters = Lists.newArrayList();
            for (String name : dataModel.value()) {
                TestCase testCase = ymlParse.getTestCase(name);
                AssertUtils.notNull(testCase, dataModel.value()[0] + ": yml文件未找到该name");
                //加入原始testCase
                testCasesByParameters.add(testCase);
                //parameters分支
                if (testCase.getParameters() != null && testCase.getParameters().size() > 0) {
                    testCasesByParameters.addAll(testCaseParametersProcess(testCase));
                }
            }

            BaseModel[][] baseModels = new BaseModel[testCasesByParameters.size()][];
            for (int i = 0; i < baseModels.length; i++) {
                String type = testCasesByParameters.get(i).getType();
                ModelType ymlType = ModelType.get(type);
                AssertUtils.notNull(ymlType, "type类型错误: %s", type);
                switch (ymlType) {
                    case FORM:
                        FormModel[] formModelsRow = new FormModel[1];
                        FormModel formModel = TestCase2BaseModel.toFormModel(testCasesByParameters.get(i));
                        formModelsRow[0] = formModel;
                        baseModels[i] = formModelsRow;
                        break;
                    case JSON:
                        JsonModel[] jsonModelsRow = new JsonModel[1];
                        JsonModel jsonModel = TestCase2BaseModel.toJsonModel(testCasesByParameters.get(i));
                        jsonModelsRow[0] = jsonModel;
                        baseModels[i] = jsonModelsRow;
                        break;
                    case XML:
                        XmlModel[] xmlModelsRow = new XmlModel[1];
                        XmlModel xmlModel = TestCase2BaseModel.toXmlModel(testCasesByParameters.get(i));
                        xmlModelsRow[0] = xmlModel;
                        baseModels[i] = xmlModelsRow;
                        break;
                    case TEXT:
                        TextModel[] textModelsRow = new TextModel[1];
                        TextModel textModel = TestCase2BaseModel.toTextModel(testCasesByParameters.get(i));
                        textModelsRow[0] = textModel;
                        baseModels[i] = textModelsRow;
                        break;
                    default:
                        break;
                }
            }
            return baseModels;
        } else {
            MultipleModel[][] multipleModels = new MultipleModel[1][];
            MultipleModel[] multipleModelsRow = new MultipleModel[1];
            MultipleModel<BaseModel> multipleModel = new MultipleModel<>();
            Map<String, BaseModel> data = Maps.newHashMap();

            //yml文件加载全部组装进MultipleModel, 避免value太多
            List<TestCase> testCases = ymlParse.getTestCases();
            List<TestCase> testCasesByParameters = Lists.newArrayList();
            testCases.forEach(testCase -> {
                if (testCase.getParameters() != null && testCase.getParameters().size() > 0) {
                    List<TestCase> cases = testCaseParametersProcess(testCase);
                    if (cases.stream().filter(c -> StringUtils.isNotBlank(c.getName())).map(TestCase::getName).collect(Collectors.toList()).contains(testCase.getName())) {
                        throw new BizException("yml中有重复name,请检查: " + testCase.getName());
                    }
                    testCasesByParameters.addAll(cases.stream().filter(c -> StringUtils.isNotBlank(c.getName())).collect(Collectors.toList()));
                }
            });

            Stream.concat(testCases.stream(), testCasesByParameters.stream()).forEach(testCase -> {
                ModelType ymlType = ModelType.get(testCase.getType());
                AssertUtils.notNull(ymlType, "type类型错误: %s", testCase.getType());
                switch (ymlType) {
                    case FORM:
                        FormModel formModel = TestCase2BaseModel.toFormModel(testCase);
                        data.put(testCase.getName(), formModel);
                        break;
                    case JSON:
                        JsonModel jsonModel = TestCase2BaseModel.toJsonModel(testCase);
                        data.put(testCase.getName(), jsonModel);
                        break;
                    case XML:
                        XmlModel xmlModel = TestCase2BaseModel.toXmlModel(testCase);
                        data.put(testCase.getName(), xmlModel);
                        break;
                    case TEXT:
                        TextModel textModel = TestCase2BaseModel.toTextModel(testCase);
                        data.put(testCase.getName(), textModel);
                        break;
                    default:
                        break;
                }
            });

            multipleModel.setData(data);
            multipleModelsRow[0] = multipleModel;
            multipleModels[0] = multipleModelsRow;
            return multipleModels;
        }

    }

    private static Object[][] wildcardMatcher(Object[][] params) {
        for (Object[] args : params) {
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof String) {
                    String arg = (String) args[i];
                    if ("null".equals(arg)) {
                        arg = null;
                    }
                    args[i] = PatternUtil.replace(arg);
                }
            }
        }
        return params;
    }

    private static List<TestCase> testCaseParametersProcess(TestCase testCase) {
        List<TestCase> cases = Lists.newArrayListWithCapacity(testCase.getParameters().size());
        //parameters中的所有testCase加入, 总执行次数testCase.getParameters().size()
        testCase.getParameters().forEach(parameter -> {
            TestCase testCaseTmp = JSONSerializerUtil.copy(testCase, TestCase.class);
            // parameter中的headers与requests节点是会进行递归补全的,requestsList直接取,不进行补全
            if (parameter.getHeaders() != null && parameter.getHeaders().size() > 0) {
                if (testCaseTmp.getHeaders() == null) {
                    testCaseTmp.setHeaders(parameter.getHeaders());
                } else {
                    recursionSetValue(testCaseTmp.getHeaders(), parameter.getHeaders());
                }
            }
            if (parameter.getRequests() != null) {
                if (testCaseTmp.getRequests() == null) {
                    testCaseTmp.setRequests(parameter.getRequests());
                } else if (parameter.getRequests() instanceof Map && testCaseTmp.getRequests() instanceof Map) {
                    // map类型 递归替换的
                    recursionSetValue((Map<String, Object>) testCaseTmp.getRequests(), (Map<String, Object>) parameter.getRequests());
                } else if (parameter.getRequests() instanceof List && testCaseTmp.getRequests() instanceof List) {
                    //list类型 无法获取list下标,只能全部替换
                    testCaseTmp.setRequests(parameter.getRequests());
                } else if (parameter.getRequests() instanceof String && testCaseTmp.getRequests() instanceof String) {
                    //String类型直接替换
                    testCaseTmp.setRequests(parameter.getRequests());
                } else {
                    throw new BizException("parameters.requests解析失败,请检查parameters.requests节点数据类型");
                }

            }

            if (parameter.getValidate() != null && parameter.getValidate().size() > 0) {
                testCaseTmp.setValidate(parameter.getValidate());
            }

            if (parameter.getValidate() != null && parameter.getValidate().size() > 0) {
                testCaseTmp.setValidate(parameter.getValidate());
            }

            if (StringUtils.isNotBlank(parameter.getName())) {
                testCaseTmp.setName(parameter.getName());
            } else {
                testCaseTmp.setName(null);
            }

            if (StringUtils.isNotBlank(parameter.getDescription())) {
                testCaseTmp.setDescription(parameter.getDescription());
            }
            //case有失败用例,所以有可能取不到结果值,去掉,如果需要缓存值,建议硬编码缓存
            testCaseTmp.setSaveGlobal(null);
            testCaseTmp.setSaveMethod(null);
            testCaseTmp.setSaveThread(null);
            cases.add(testCaseTmp);
        });
        return cases;
    }

    private static void recursionSetValue(Map<String, Object> originalMap, Map<String, Object> increasedMap) {
        increasedMap.forEach((k, v) -> {
            originalMap.putIfAbsent(k, v);
            if (originalMap.containsKey(k)) {
                if (originalMap.get(k) instanceof Map && v instanceof Map) {
                    recursionSetValue((Map<String, Object>) originalMap.get(k), (Map<String, Object>) v);
                } else {
                    originalMap.put(k, v);
                }
            } else {
                originalMap.put(k, v);
            }
        });
    }
}
