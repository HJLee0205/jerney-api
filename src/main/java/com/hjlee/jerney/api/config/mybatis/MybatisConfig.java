package com.hjlee.jerney.api.config.mybatis;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@MapperScan(
        value = "com.hjlee.jerney.**.mapper",
        annotationClass = Mapper.class,
        sqlSessionFactoryRef = "mainSqlSessionFactory"
)
@RequiredArgsConstructor
public class MybatisConfig {
//    private final Environment environment;

    @Primary
    @Bean(name = "mainDataSource")
    @ConfigurationProperties(prefix = "spring.datasource-oracle")
    public DataSource mainDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "mainSqlSessionFactory")
    public SqlSessionFactory mainSqlSessionFactory(@Qualifier("mainDataSource") DataSource mainDataSource, ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(mainDataSource);
        sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath*:/com/hjlee/jerney/api/**/mapper/*.xml"));
        sqlSessionFactoryBean.setTypeAliasesPackage("com.hjlee.jerney.**.vo, com.hjlee.jerney.**.dto");

        org.apache.ibatis.session.Configuration config = new org.apache.ibatis.session.Configuration();
        config.setCacheEnabled(true);
        config.setMapUnderscoreToCamelCase(true);
        config.setCallSettersOnNulls(true);
        config.setJdbcTypeForNull(JdbcType.NULL);
        config.setLocalCacheScope(LocalCacheScope.STATEMENT);
//        config.setObjectWrapperFactory(new MapWrapperFactory());

        sqlSessionFactoryBean.setConfiguration(config);

        return sqlSessionFactoryBean.getObject();
    }

    @Primary
    @Bean(name = "mainSqlSessionTemplate")
    public SqlSessionTemplate mainSqlSessionTemplate(@Qualifier("mainSqlSessionFactory") SqlSessionFactory mainSqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(mainSqlSessionFactory);
    }
}
