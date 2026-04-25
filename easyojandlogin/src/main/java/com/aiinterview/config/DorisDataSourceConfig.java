/*
package com.aiinterview.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

*/
/**
 * Doris 只读数据源配置。
 * 采用 MySQL 协议连接 Doris FE，适合统计、报表、分析类查询。
 *//*

@Configuration
public class DorisDataSourceConfig {

    @Bean(name = "dorisDataSource")
    @ConfigurationProperties(prefix = "app.datasource.doris")
    public DataSource dorisDataSource() {
        return new DruidDataSource();
    }

    @Bean(name = "dorisJdbcTemplate")
    public JdbcTemplate dorisJdbcTemplate(@Qualifier("dorisDataSource") DataSource dorisDataSource) {
        return new JdbcTemplate(dorisDataSource);
    }
}*/
