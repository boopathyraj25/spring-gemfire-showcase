/*
 *
 *  * Copyright 2023 VMware, Inc.
 *  * SPDX-License-Identifier: GPL-3.0
 *
 */

package spring.gemfire.showcase.account.sink;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;

@Configuration
@Slf4j
public class RabbitConfig {

    @Value("${spring.application.name}")
    private String applicationName;


    @Value("${spring.rabbitmq.username:guest}")
    private String username = "guest";

    @Value("${spring.rabbitmq.password:guest}")
    private String password   = "guest";

    @Value("${spring.rabbitmq.host:34.121.19.122}")
    private String hostname = "34.121.19.122";

    @Bean
    ConnectionNameStrategy connectionNameStrategy(){
        return (connectionFactory) -> applicationName;
    }


    @Bean
    MessageConverter messageConverter()
    {
        return new MappingJackson2MessageConverter();
    }

}
