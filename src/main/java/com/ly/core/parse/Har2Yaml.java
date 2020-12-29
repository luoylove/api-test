package com.ly.core.parse;

import com.google.common.base.Splitter;
import com.ly.core.enums.HttpType;
import com.ly.core.enums.MatchesEnum;
import com.ly.core.enums.ModelType;
import com.ly.core.exception.BizException;
import com.ly.core.utils.JSONSerializerUtil;
import com.ly.core.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.testng.collections.Maps;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: luoy
 * @Date: 2020/5/9 17:34.
 */
public class Har2Yaml {
    private static final List<String> SKIP_HEADERS = Lists.newArrayList(":method", ":scheme", "user-agent", "accept",
            "accept-encoding", ":path", ":authority", "if-modified-since", "content-length","sec-fetch-site", "sec-fetch-mode",
            "sec-fetch-dest", "referer", "accept-language");

    /**
     * 生成路径和名字直接在har文件同级目录和名字,且每次执行都是覆盖
     * @param harPath
     */
    public static void toYaml(String harPath) {
        //生成路径和名字直接在har文件同级目录和名字
        String toYamlPath = harPath.replace(".har", ".yml");
        toYaml(harPath, toYamlPath, true);
    }

    /**
     * 指定生成yml路径与名字,且每次都是往yml中最后追加
     * @param harPath
     * @param toYamlPath
     * @param isReplace 是否覆盖 true: 覆盖, false: 追加
     */
    public static void toYaml(String harPath, String toYamlPath, boolean isReplace) {
        DataEntity dataEntity = new DataEntity();
        List<TestCase> testCases = Lists.newArrayList();

        String harJson = Utils.read(harPath, "UTF-8");

        JsonPath jsonPath = JsonPath.create(harJson);

        int entriesInt = jsonPath.size("log.entries");

        for (int i = 0; i < entriesInt; i++) {
            //当前对象jsonPath
            JsonPath currentJsonPath = getCurrentJsonPath(jsonPath, i);

            String url = getUrl(currentJsonPath);

            String httpMethod = getHttpMethod(currentJsonPath);

            String name = getName(url, httpMethod);

            String type = getModelType(currentJsonPath);

            Map<String, Object> headers = getHeaders(currentJsonPath);

            Map<String, Object> requests = getRequests(currentJsonPath);

            Map<String, List<Object>> validate = getValidate(currentJsonPath);

            TestCase testCase = TestCase.builder().name(name).type(type).description(name).headers(headers)
                    .url(url).method(httpMethod).requests(requests).validate(validate).build();
            testCases.add(testCase);
        }
        dataEntity.setTestCase(testCases);

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        options.setIndent(4);
        CustomPropertyUtils customPropertyUtils = new CustomPropertyUtils();
        Representer customRepresenter = new Representer();
        customRepresenter.setPropertyUtils(customPropertyUtils);
        Yaml yaml = new Yaml(customRepresenter, options);
        String dump = yaml.dumpAs(dataEntity, Tag.MAP, null);
        Utils.write(toYamlPath, dump, !isReplace);
    }

    private static JsonPath getCurrentJsonPath(JsonPath jsonPath, int i) {
        return JsonPath.create(String.valueOf(jsonPath.get(getBasePath(i))));
    }

    private static String getBasePath(int i) {
        return String.format("log.entries[%d].", i);
    }

    private static String getUrl(JsonPath jsonPath) {
        return String.valueOf(jsonPath.get("request.url"));
    }

    private static Map<String, Object> getHeaders(JsonPath jsonPath) {
        Map<String, Object> headers = Maps.newHashMap();
        List<Map<String, String>> harHeaders = (List<Map<String, String>>) jsonPath.get("request.headers");
        if (harHeaders == null && harHeaders.isEmpty()) {
            return headers;
        }
        harHeaders.stream().filter(map -> !SKIP_HEADERS.contains(map.get("name"))).forEach(map -> headers.put(map.get("name"), map.get("value")));
        return headers;
    }

    /** swagger style name */
    private static String getName(String url, String method) {
        String name = "%sUsing" + method.toUpperCase();
        if (StringUtils.isNotBlank(url)) {
            List<String> list = Splitter.on("/").splitToList(url).stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
            if (list.size() == 0) {
                return String.format(name, url);
            } else if (list.size() == 1){
                return String.format(name, list.get(list.size() - 1));
            } else {
                if (list.get(list.size() - 1).length() <= 2) {
                    String firstName = list.get(list.size() - 2);
                    String lastName = list.get(list.size() - 1);
                    String lastNameHeader = lastName.substring(0, 1);
                    String lastNameBody = lastName.substring(1);
                    String preName = firstName + lastNameHeader.toUpperCase() + lastNameBody.toLowerCase();
                    return String.format(name, preName);
                } else {
                    return String.format(name, list.get(list.size() - 1));
                }
            }
        }
        return "";
    }

    private static Map<String ,Object> getRequests(JsonPath jsonPath) {
        if (getHttpMethod(jsonPath).toLowerCase().equals(HttpType.GET.getType())) {
            return null;
        }

        List<Map<String, String>> queryString = (List<Map<String, String>>) jsonPath.get("request.queryString");
        if(queryString != null && !queryString.isEmpty()) {
            Map<String, Object> requests = Maps.newHashMap();
            for(Map<String, String> map: queryString) {
                requests.put(map.get("name"), map.get("value"));
            }
            return requests;
        }
        boolean existPostData = jsonPath.isExist("request.postData");

        if (existPostData) {
            String postData = String.valueOf(jsonPath.get("request.postData"));
            if (StringUtils.isBlank(postData)) {
                return null;
            }

            String mimeType = String.valueOf(jsonPath.get("request.postData.mimeType"));
            if (mimeType == null || !mimeType.contains("json")) {
                throw new BizException("目前只支持JSON请求模式解析");
            }
            String jsonRequests = String.valueOf(jsonPath.get("request.postData.text"));
            if (StringUtils.isNotBlank(jsonRequests)) {
                return JSONSerializerUtil.unSerialize(jsonRequests, Map.class);
            }
        }
        return null;
    }

    private static String getModelType(JsonPath jsonPath) {
        if (getHttpMethod(jsonPath).toLowerCase().equals(HttpType.GET.getType())) {
            return ModelType.FORM.getType();
        }
        boolean existPostData = jsonPath.isExist("request.postData");

        if (existPostData) {
            String postData = String.valueOf(jsonPath.get("request.postData"));
            if (StringUtils.isBlank(postData)) {
                return ModelType.FORM.getType();
            }

            String mimeType = String.valueOf(jsonPath.get("request.postData.mimeType"));
            if (mimeType != null || mimeType.contains("json")) {
                return ModelType.JSON.getType();
            }
        }
        return ModelType.FORM.getType();
    }

    private static Map<String, List<Object>> getValidate(JsonPath jsonPath) {
        String response = String.valueOf(jsonPath.get("response.content.text"));
        if (StringUtils.isBlank(response)) {
            return null;
        }

        String mimeType = String.valueOf(jsonPath.get("response.content.mimeType"));
        if (mimeType == null ) {
            return null;
        }

        if (!mimeType.contains("json")) {
            throw new BizException("目前只支持JSON返回模式解析");
        }

        if (jsonPath.isExist("response.content.encoding")) {
            //只处理了base64
            if (String.valueOf(jsonPath.get("response.content.encoding")).toLowerCase().equals("base64")) {
                response = responseDecode(response);
            }
        }

        Map<String, Object> resMap = JSONSerializerUtil.unSerialize(response, Map.class);
        Map<String, List<Object>> validate = Maps.newHashMap();
        List<Object> eq = Lists.newArrayList();
        Map<String, Object> eqDetail = Maps.newHashMap();
        resMap.forEach( (k,v) -> {
            if (!(v instanceof Collection) && !(v instanceof Map)) {
                eqDetail.put(k, v);
            }
        });
        eq.add(eqDetail);
        validate.put(MatchesEnum.EQ.getType(), eq);
        return validate;
    }

    private static String getHttpMethod(JsonPath jsonPath) {
        return String.valueOf(jsonPath.get("request.method"));
    }

    private static String responseDecode(String encode) {
        return new String(Base64.getDecoder().decode(encode));
    }
}
