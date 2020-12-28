package com.ly.core.utils;

import com.ly.core.parse.Har2Yaml;
import com.ly.core.parse.SwaggerParse;
import lombok.extern.slf4j.Slf4j;

/**
 * 其他源转换成yaml
 * @Author: luoy
 * @Date: 2020/12/28 10:13.
 */
@Slf4j
public class Source2Yaml {
    /**
     * har文件生成yaml
     * 指定生成yml路径与名字,且每次都是往yml中最后追加
     * @param harPath
     * @param toYamlPath
     * @param isReplace 是否覆盖 true: 覆盖, false: 追加
     */
    public static void harToYaml(String harPath, String toYamlPath, boolean isReplace) {
        Har2Yaml.toYaml(harPath, toYamlPath, isReplace);
        log.info("harToYml success!");
    }

    /**
     * har文件生成yaml
     * 生成路径和名字直接在har文件同级目录和名字,且每次执行都是覆盖
     * @param harPath
     */
    public static void harToYaml(String harPath) {
        Har2Yaml.toYaml(harPath);
        log.info("harToYml success!");
    }

    /**
     * swagger生成yaml
     * @param isReplace 是否覆盖 true: 覆盖, false: 追加
     * @param swaggerPath 兼容swaggerUrl, localSwaggerJson, file
     * @param writePath
     */
    public static void swaggerToYaml(String swaggerPath, String writePath, boolean isReplace) {
        new SwaggerParse().parse(swaggerPath, writePath, isReplace);
        log.info("swaggerToYml success!");
    }
}
