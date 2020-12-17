package com.ly.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: luoy
 * @Date: 2019/8/2 10:52.
 *
 * <p>数据驱动 yaml模式
 * <p> 当Format= SINGLE, vaule = {"login", "login1"}
 *     表示该用例是单接口模式
 *     用例入参模型为: BaseModel
 *     入参值为yaml文档name对应的login与login1和这两个name下的所有parameters
 *     eg.
 *     @DataModel(value = {"login", "login1"}, format = DataModel.Format.SINGLE)
 *     public void login(BaseModel model) {
 *         apiClient.doHttp(model).auto();
 *     }
 *
 * <p> 当Format= MULTIPLE, vaule = {"login", "profile"}
 *     表示该用例是业务流模式,只会运行一次,业务对应入参可从
 *     用例入参模型为: MultipleModel
 *     value可省略
 *     eg.
 *     @DataModel(format = DataModel.Format.MULTIPLE)
 *     public void login1(MultipleModel model) {
 *         defaultApiClient.doHttp(model.getModel("login")).auto();
 *         defaultApiClient.doHttp(model.getModel("profile")).auto();
 *     }
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD})
public @interface DataModel {
    /** 取yaml的name */
    String[] value() default {};

    /** yaml文件名字,支持多个文件引入,默认路径为 resources下的data.yml */
    String[] path() default {"data.yml"};

    /** 模式 */
    DataModel.Format format() default Format.MULTIPLE;

    enum Format
    {
        /**单接口**/
        SINGLE,
        /**串行**/
        MULTIPLE
    }
}
