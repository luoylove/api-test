package com.ly.core.config;

import com.ly.core.annotation.HttpServer;
import com.ly.core.exception.BizException;
import com.ly.core.proxy.ProxyFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import java.util.Set;

/**
 * @Author: luoy
 * @Date: 2019/9/6 14:18.
 */
public class ApiBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    private String[] scanning;

    public ApiBeanDefinitionRegistryPostProcessor(String...scanning) {
        this.scanning = scanning;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        //创建扫描类
        ApiClassPathScanningCandidateComponentProvider beanScanner = new ApiClassPathScanningCandidateComponentProvider(HttpServer.class);
        //创建自己扫描规则，扫描所有带@HttpServer注解的类

        Set<BeanDefinition> beanDefinitions = beanScanner.findCandidateComponents(scanning);
        for (BeanDefinition beanDefinition : beanDefinitions) {
            Class<?> clazz;
            try {
                clazz = Class.forName(beanDefinition.getBeanClassName());
            } catch (ClassNotFoundException e) {
                throw new BizException("class not found:" + beanDefinition.getBeanClassName());
            }
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(ProxyFactoryBean.class);
            String beanName = beanDefinition.getBeanClassName();
            beanDefinitionBuilder.addPropertyValue("clazz", clazz);

            BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinitionBuilder.getBeanDefinition(), beanName);
            BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }
}