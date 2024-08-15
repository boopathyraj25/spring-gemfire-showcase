/*
 *
 *  * Copyright 2023 VMware, Inc.
 *  * SPDX-License-Identifier: GPL-3.0
 *
 */

package spring.gemfire.batch.account.batch;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;

@Slf4j
@Component
@EnableJpaRepositories
@EnableTransactionManagement
public class JdbcConfig implements BeanPostProcessor {
    @Value("${db.schema}")
    private String schemaName;

    @Value("${batch.jdbc.url}")
    private String batchJdbcUrl;

    @Value("${batch.jdbc.username}")
    private String batchUsername;


    @Value("${batch.jdbc.password:''}")
    private String batchPassword;

    @Value("${database.schema.create:'false'}")
    private boolean cleanDatabase;

    @SneakyThrows
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataSource dataSource) {
            var dataSource1 = DataSourceBuilder.create().
                    url(batchJdbcUrl).username(batchUsername)
                    .password(batchPassword).build();
            log.info("Connecting to DB " + batchJdbcUrl);
            try (var conn = dataSource1.getConnection();
                 var statement = conn.createStatement();
            var deleteStatement = conn.createStatement()) {
                if (cleanDatabase){
                    log.info("Going to delete DB schema '{}' if not exists.", schemaName);
                    deleteStatement.execute("DROP SCHEMA IF EXISTS " + schemaName + " CASCADE");
                }
                log.info("Going to create DB schema '{}' if not exists.", schemaName);
                statement.execute("create schema if not exists " + schemaName);
                log.info("Created schema for app in " + batchJdbcUrl + " DB");
            } catch (SQLException e) {
                log.info("failed to create schema in " + batchJdbcUrl);
                throw new RuntimeException("Failed to create schema '" + schemaName + "'", e);
            }
        }
        return bean;
    }

}
