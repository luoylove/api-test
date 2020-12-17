package com.ly.core.config;

import com.ly.core.db.BaseDbServer;
import com.ly.core.db.MysqlServer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

/**
 * @Author: luoy
 * @Date: 2019/8/30 9:58.
 */
//@Configuration
//懒加载，当第一次使用这个bean的时候才加载进容器，目的为2点，
// 1：减少IOC容器启动时间
// 2：当不需要操作mysql时候可不配mysql参数
// 3: 可直接用JdbcTemplate
//@Lazy
public class DbConfig {
    //给数据源注入springboot容器，并且指定bean名，方便注入
    @Bean("qa-dataSource")
    @ConfigurationProperties(prefix = "spring.datasource.qa")
    public DataSource createAtDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean("uat-dataSource")
    @ConfigurationProperties(prefix = "spring.datasource.uat")
    public DataSource createAsDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean("qa")
    public BaseDbServer createCarrier(@Qualifier("qa-dataSource")DataSource dataSource) {
        return new MysqlServer(dataSource);
    }

    @Bean("uat")
    public BaseDbServer createUat(@Qualifier("uat-dataSource")DataSource dataSource) {
        return new MysqlServer(dataSource);
    }
}
