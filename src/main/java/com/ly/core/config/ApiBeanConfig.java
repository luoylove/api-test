package com.ly.core.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: luoy
 * @Date: 2019/8/26 16:03.
 *
 */
@Configuration
public class ApiBeanConfig {

    /**
     * api框架中需要扫描自动注入的api接口路径
     */
    private static final String[] scanning = {""};

    @ConditionalOnMissingBean(ApiBeanDefinitionRegistryPostProcessor.class)
    @Bean
    public static ApiBeanDefinitionRegistryPostProcessor apiBeanDefinitionRegistryPostProcessor() {
        return new ApiBeanDefinitionRegistryPostProcessor(scanning);
    }
}
