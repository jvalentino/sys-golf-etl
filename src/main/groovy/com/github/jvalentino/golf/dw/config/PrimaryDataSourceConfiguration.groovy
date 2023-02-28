package com.github.jvalentino.golf.dw.config

import com.zaxxer.hikari.HikariDataSource
import groovy.transform.CompileDynamic
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

/**
 * To have two data sources with JPA, you have to do this mess.
 * https://mmafrar.medium.com/
 * configuring-multiple-data-sources-with-spring-boot-2-and-spring-data-jpa-8e236844e80f
 * @author john.valentino
 */
@CompileDynamic
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = 'primaryEntityManagerFactory',
        transactionManagerRef = 'primaryTransactionManager',
        basePackages = ['com.github.jvalentino.golf.dw.repo'])
class PrimaryDataSourceConfiguration {

    @Value('${hibernate.dialect-primary}')
    String dialect

    @Primary
    @Bean(name = 'primaryDataSourceProperties')
    @ConfigurationProperties('spring.datasource-primary')
    DataSourceProperties primaryDataSourceProperties() {
        new DataSourceProperties()
    }

    @Primary
    @Bean(name = 'primaryDataSource')
    @ConfigurationProperties('spring.datasource-primary.configuration')
    DataSource primaryDataSource(@Qualifier('primaryDataSourceProperties')
                                         DataSourceProperties primaryDataSourceProperties) {
        primaryDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource).build()
    }

    @Primary
    @Bean(name = 'primaryEntityManagerFactory')
    LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(
            EntityManagerFactoryBuilder primaryEntityManagerFactoryBuilder,
            @Qualifier('primaryDataSource') DataSource primaryDataSource) {

        Map<String, String> primaryJpaProperties = [:]
        primaryJpaProperties.put('hibernate.dialect', dialect)
        primaryJpaProperties.put('hibernate.hbm2ddl.auto', 'update')
        primaryJpaProperties.put('spring.jpa.generate-ddl', true)

        primaryEntityManagerFactoryBuilder
                .dataSource(primaryDataSource)
                .packages('com.github.jvalentino.golf.dw.entity')
                .persistenceUnit('primaryDataSource')
                .properties(primaryJpaProperties)
                .build()
    }

    @Primary
    @Bean(name = 'primaryTransactionManager')
    PlatformTransactionManager primaryTransactionManager(
            @Qualifier('primaryEntityManagerFactory') EntityManagerFactory primaryEntityManagerFactory) {
        new JpaTransactionManager(primaryEntityManagerFactory)
    }

}
