package com.ly.api;

import com.ly.core.base.BaseTestCase;
import com.ly.plugin.PluginSupport;
import io.qameta.allure.Feature;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: luoy
 * @Date: 2020/3/27 16:09.
 */
//Features:标注主要功能模块
//该类用于引入必要client
//主要通过模块base类来标注，该模块下所有都继承该base类
@Feature("test-api")
public abstract class BaseTestApiTestCase extends BaseTestCase {

    @Autowired
    protected PluginSupport support;

    @Autowired
    protected TestApiClient testApiClient;

    @Autowired
    protected DefaultApiClient apiClient;

}
