package com.ly.core.parse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ly.core.enums.MatchesEnum;
import com.ly.core.enums.ModelType;
import com.ly.core.utils.Utils;
import io.restassured.http.ContentType;
import io.swagger.models.ArrayModel;
import io.swagger.models.HttpMethod;
import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.RefModel;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.FormParameter;
import io.swagger.models.parameters.HeaderParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.PathParameter;
import io.swagger.models.parameters.QueryParameter;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.BaseIntegerProperty;
import io.swagger.models.properties.DecimalProperty;
import io.swagger.models.properties.DoubleProperty;
import io.swagger.models.properties.FloatProperty;
import io.swagger.models.properties.IntegerProperty;
import io.swagger.models.properties.LongProperty;
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.parser.SwaggerParser;
import org.testng.collections.Maps;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


/**
 * @Author: luoy
 * @Date: 2020/12/22 13:52.
 */
public class SwaggerParse {
    public static final String PATH = "path";
    public static final String FORM_DATA = "formData";
    public static final String FILE = "file";
    public static final String HEADER = "header";
    public static final String BODY = "body";
    public static final String COOKIE = "cookie";
    public static final String QUERY = "query";

    private Map<String, Model> definitions ;

    /**
     * @param isReplace 是否覆盖 true: 覆盖, false: 追加
     * @param swaggerPath 兼容swaggerUrl, localSwaggerJson, file
     * @param writePath
     */
    public void parse(String swaggerPath, String writePath, boolean isReplace) {
        SwaggerParser swaggerParser = new SwaggerParser();
        Swagger swagger = swaggerParser.read(swaggerPath);
        this.definitions = swagger.getDefinitions();
        Map<String, Path> paths = swagger.getPaths();
        List<TestCase> testCases = Lists.newArrayListWithCapacity(paths.size());

        for (String path : paths.keySet()) {
            TestCase testCase = new TestCase();
            //初始化
            testCase.setRequests(Maps.newHashMap());
            testCase.setHeaders(Maps.newHashMap());

            Map<HttpMethod, Operation> operationMap = swagger.getPath(path).getOperationMap();
            for (HttpMethod method : operationMap.keySet()) {
                //设置method
                testCase.setMethod(method.name());
                Operation operation = operationMap.get(method);
                List<Parameter> parameters = operation.getParameters();
                testCase.setDescription(Optional.ofNullable(operation.getDescription()).orElse(operation.getSummary()));
                testCase.setUrl(path);
                testCase.setName(operation.getOperationId());
                List<String> list = operation.getConsumes();
                testCase.setType(list == null || list.size() <= 0 ? "form" : buildType(list.get(0)));
                //req
                buildRequests(parameters, testCase);
                buildValidate(operation.getResponses(), testCase);
            }
            System.out.println(testCase.toString());
            testCases.add(testCase);
        }
        DataEntity dataEntity = new DataEntity();
        dataEntity.setTestCase(testCases);
        write(writePath, dataEntity, isReplace);
    }

    private void write(String writePath, DataEntity dataEntity, boolean isReplace) {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
//        options.setPrettyFlow(true);
        options.setIndent(4);
        CustomPropertyUtils customPropertyUtils = new CustomPropertyUtils();
        Representer customRepresenter = new RepresenterNotNull();
        customRepresenter.setPropertyUtils(customPropertyUtils);
        Yaml yaml = new Yaml(customRepresenter, options);
        String dump = yaml.dumpAs(dataEntity, Tag.MAP, null);
        Utils.write(writePath, dump, !isReplace);
    }

    private  void buildRequests(List<Parameter> parameters, TestCase testCase) {
        for (Parameter parameter : parameters) {
            switch (parameter.getIn()) {
                case PATH:
                    buildPath(parameter, testCase);
                    break;
                case QUERY:
                    buildQuery(parameter, testCase);
                    break;
                case FORM_DATA:
                    buildFormData(parameter, testCase);
                    break;
                case BODY:
                    buildBody(parameter, testCase);
                    break;
                case HEADER:
                    buildHeaders(parameter, testCase);
                    break;
                case COOKIE:
                    break;
                case FILE:
                    break;
                default:
                    break;
            }
        }
    }

    private void buildValidate(Map<String, Response> responseMap, TestCase testCase) {
        if (responseMap != null) {
            responseMap.forEach((key, response) -> {
                Model schema = response.getResponseSchema();
                Map<String, List<Object>> validate = Maps.newHashMap();
                if (schema instanceof RefModel) {
                    String simpleRef;
                    RefModel refModel = (RefModel) schema;
                    String originalRef = refModel.getOriginalRef();
                    if (refModel.getOriginalRef().split("/").length > 3) {
                        simpleRef = originalRef.replace("#/definitions/", "");
                    } else {
                        simpleRef = refModel.getSimpleRef();
                    }
                    Model model = this.definitions.get(simpleRef);
                    HashSet<String> refSet = new HashSet<>();
                    refSet.add(simpleRef);
                    if (Objects.nonNull(model)) {
                        JSONObject body = getBodyParameters(model.getProperties(), refSet);
                        Map<String, Object> resMap = JSONObject.toJavaObject(body, Map.class);
                        List<Object> eq = Lists.newArrayList();
                        Map<String, Object> eqDetail = Maps.newHashMap();
                        resMap.forEach( (k,v) -> {
                            if (!(v instanceof Collection) && !(v instanceof Map) && v != null) {
                                eqDetail.put(k, v);
                            }
                        });
                        eq.add(eqDetail);
                        validate.put(MatchesEnum.EQ.getType(), eq);
                    }
                } else if(schema instanceof ArrayList){
                    List<Object> len = Lists.newArrayList();
                    Map<String, Object> lenDetail = Maps.newHashMap();
                    //从根判断list长度为1,需要在yml手动校准
                    lenDetail.put(".", "1");
                    len.add(lenDetail);
                    validate.put(MatchesEnum.LEN.getType(), len);
                } else {
                    // 如果其他类型默认不断言
                    return;
                }
                testCase.setValidate(validate);
            });
        }
    }

    private void buildHeaders(Parameter parameter, TestCase testCase) {
        HeaderParameter headerParameter = (HeaderParameter) parameter;
        Map<String, Object> headers = testCase.getHeaders();
        //默认值为空的话,把description与需要填写的类型当值填入
        headers.put(headerParameter.getName(),
                Optional.ofNullable(headerParameter.getDefaultValue()).orElse(headerParameter.getDescription() + "-" + headerParameter.getType()));
    }

    private void buildQuery(Parameter parameter, TestCase testCase){
        QueryParameter queryParameter = (QueryParameter) parameter;
        Map<String, Object> requests = testCase.getRequests();
        requests.put(parameter.getName(), Optional.ofNullable(queryParameter.getDefaultValue()).orElse(queryParameter.getDescription() + "-" + queryParameter.getType()));
    }

    private void buildFormData(Parameter parameter, TestCase testCase){
        FormParameter formParameter = (FormParameter) parameter;
        Map<String, Object> requests = testCase.getRequests();
        requests.put(formParameter.getName(), Optional.ofNullable(formParameter.getDefaultValue()).orElse(formParameter.getDescription() + "-" + formParameter.getType()));
    }

    private void buildPath(Parameter parameter, TestCase testCase){
        PathParameter pathParameter = (PathParameter) parameter;
        Map<String, Object> requests = testCase.getRequests();
        requests.put(pathParameter.getName(), Optional.ofNullable(pathParameter.getDefaultValue()).orElse(pathParameter.getDescription() + "-" + pathParameter.getType()));
    }

    private void buildBody(Parameter parameter, TestCase testCase){
        BodyParameter bodyParameter = (BodyParameter) parameter;
        Model schema = bodyParameter.getSchema();
        schema(schema, testCase);
    }

    private void schema(Model schema, TestCase testCase) {
        if (schema instanceof RefModel) {
            String simpleRef;
            RefModel refModel = (RefModel) schema;
            String originalRef = refModel.getOriginalRef();
            if (refModel.getOriginalRef().split("/").length > 3) {
                simpleRef = originalRef.replace("#/definitions/", "");
            } else {
                simpleRef = refModel.getSimpleRef();
            }
            Model model = this.definitions.get(simpleRef);
            HashSet<String> refSet = new HashSet<>();
            refSet.add(simpleRef);
            if (Objects.nonNull(model)) {
                JSONObject bodyParameters = getBodyParameters(model.getProperties(), refSet);
                testCase.setRequests(JSONObject.toJavaObject(bodyParameters, Map.class));
            }
        } else if (schema instanceof ArrayModel) {
            List<Object> requestsList = Lists.newArrayList();
            //模型数组
            ArrayModel arrayModel = (ArrayModel) schema;
            Property items = arrayModel.getItems();
            if (items instanceof RefProperty) {
                RefProperty refProperty = (RefProperty) items;
                String simpleRef = refProperty.getSimpleRef();
                HashSet<String> refSet = new HashSet<>();
                refSet.add(simpleRef);
                Model model = definitions.get(simpleRef);
                JSONArray propertyList = new JSONArray();
                propertyList.add(getBodyParameters(model.getProperties(), refSet));
                requestsList.addAll(JSONObject.parseArray(propertyList.toJSONString(), Object.class));
            } else {
                //TODO ObjectProperty ?
                requestsList.add(items.getDescription() + "-" + items.getType());
            }
            //yml write skip null
            if(testCase.getRequests().size() == 0) {
                testCase.setRequests(null);
            }
            testCase.setRequestsList(requestsList);
        }
    }

    private String buildType(String contentType) {
        if (ContentType.JSON.matches(contentType)) {
            return ModelType.JSON.getType();
        }
        if (ContentType.XML.matches(contentType)) {
            return ModelType.XML.getType();
        }
        if (ContentType.URLENC.matches(contentType)) {
            return ModelType.FORM.getType();
        }
        return "";
    }

    private JSONObject getBodyParameters(Map<String, Property> properties, HashSet<String> refSet) {
        JSONObject jsonObject = new JSONObject();
        if (properties != null) {
            properties.forEach((key, value) -> {
                if (value instanceof ObjectProperty) {
                    ObjectProperty objectProperty = (ObjectProperty) value;
                    jsonObject.put(key, getBodyParameters(objectProperty.getProperties(), refSet));
                } else if (value instanceof ArrayProperty) {
                    getBodyParametersByArrayProperty(key, (ArrayProperty)value, jsonObject, refSet);
                } else if(value instanceof RefProperty) {
                    RefProperty refProperty = (RefProperty) value;
                    String simpleRef = refProperty.getSimpleRef();
                    refSet.add(simpleRef);
                    Model model = this.definitions.get(simpleRef);
                    jsonObject.put(key, getBodyParameters(model.getProperties(), refSet));
                } else {
                    jsonObject.put(key, Optional.ofNullable(value.getExample()).orElse(getDefaultValue(value)));
                }
            });
        }
        return jsonObject;
    }

    private void getBodyParametersByArrayProperty(String key, ArrayProperty arrayProperty, JSONObject jsonObject, HashSet<String> refSet) {
        Property items = arrayProperty.getItems();
        if (items instanceof RefProperty) {
            RefProperty refProperty = (RefProperty) items;
            String simpleRef = refProperty.getSimpleRef();
            if (refSet.contains(simpleRef)) {
                jsonObject.put(key, new JSONArray());
                return;
            }
            refSet.add(simpleRef);
            Model model = this.definitions.get(simpleRef);
            JSONArray propertyList = new JSONArray();
            propertyList.add(getBodyParameters(model.getProperties(), refSet));
            jsonObject.put(key, propertyList);
        } else if (items instanceof ArrayProperty){
            getBodyParametersByArrayProperty(key, (ArrayProperty)items, jsonObject, refSet);
        } else {
            JSONArray objects = new JSONArray(Arrays.asList(Optional.ofNullable(items.getExample()).orElse(getDefaultValue(items))));
            jsonObject.put(key, objects);
        }
    }

    private Object  getDefaultValue(Property items) {
        if (Objects.nonNull(items.getDescription())) {
            return items.getDescription() + "-" + items.getType();
        }
        if (items instanceof LongProperty || items instanceof IntegerProperty
                || items instanceof BaseIntegerProperty) {
            return 0;
        }

        if (items instanceof FloatProperty || items instanceof DoubleProperty
                || items instanceof DecimalProperty) {
            return 0.0;
        }
        return "" + "-" + items.getType();
    }
}
