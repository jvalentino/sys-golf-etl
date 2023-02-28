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
        entityManagerFactoryRef = 'secondaryEntityManagerFactory',
        transactionManagerRef = 'secondaryTransactionManager',
        basePackages = ['com.github.jvalentino.golf.rest.repo'])
class SecondaryDataSourceConfiguration {

    @Value('${hibernate.dialect-secondary}')
    String dialect

    @Bean(name = 'secondaryDataSourceProperties')
    @ConfigurationProperties('spring.datasource-secondary')
    DataSourceProperties secondaryDataSourceProperties() {
        new DataSourceProperties()
    }

    @Bean(name = 'secondaryDataSource')
    @ConfigurationProperties('spring.datasource-secondary.configuration')
    DataSource secondaryDataSource(@Qualifier('secondaryDataSourceProperties')
                                         DataSourceProperties secondaryDataSourceProperties) {
        secondaryDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource).build()
    }

    @Bean(name = 'secondaryEntityManagerFactory')
    LocalContainerEntityManagerFactoryBean secondaryEntityManagerFactory(
            EntityManagerFactoryBuilder secondaryEntityManagerFactoryBuilder,
            @Qualifier('secondaryDataSource') DataSource secondaryDataSource) {

        Map<String, String> secondaryJpaProperties = [:]
        secondaryJpaProperties.put('hibernate.dialect', dialect)
        secondaryJpaProperties.put('hibernate.hbm2ddl.auto', 'update')
        secondaryJpaProperties.put('spring.jpa.generate-ddl', true)

        secondaryEntityManagerFactoryBuilder
                .dataSource(secondaryDataSource)
                .packages('com.github.jvalentino.golf.rest.entity')
                .persistenceUnit('secondaryDataSource')
                .properties(secondaryJpaProperties)
                .build()
    }

    @Bean(name = 'secondaryTransactionManager')
    PlatformTransactionManager secondaryTransactionManager(
            @Qualifier('secondaryEntityManagerFactory') EntityManagerFactory secondaryEntityManagerFactory) {
        new JpaTransactionManager(secondaryEntityManagerFactory)
    }

}
