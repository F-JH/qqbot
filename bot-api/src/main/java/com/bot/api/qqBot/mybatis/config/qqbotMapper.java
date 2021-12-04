package com.bot.api.qqBot.mybatis.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.bot.api.qqBot.mybatis.dao.qqbot", sqlSessionFactoryRef = "qqbotSqlSessionFactory")
public class qqbotMapper {

    @Bean(name = "qqbotDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.qqbot")
    public DataSource dataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "qqbotSqlSessionFactory")
    public SqlSessionFactory sessionFactory(@Qualifier("qqbotDataSource") DataSource dataSource) throws Exception{
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        return bean.getObject();
    }

    @Bean(name = "qqbotTransactionManager")
    public DataSourceTransactionManager transactionManager(@Qualifier("qqbotDataSource") DataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "qqbotSqlSessionTemple")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("qqbotSqlSessionFactory") SqlSessionFactory sessionFactory){
        return new SqlSessionTemplate(sessionFactory);
    }
}
