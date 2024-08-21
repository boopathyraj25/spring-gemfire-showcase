/*
 *
 *  * Copyright 2023 VMware, Inc.
 *  * SPDX-License-Identifier: GPL-3.0
 *
 */

package spring.gemfire.showcase.account.csv;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.support.MessageBuilder;

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
    public MessageConverter converter(ObjectMapper objectMapper) {
        return new MessageConverter() {
            @Override
            public Object fromMessage(Message<?> message, Class<?> targetClass) {
                return message.getPayload();
            }

            @SneakyThrows
            @Override
            public Message<?> toMessage(Object payload, MessageHeaders headers) {
                return MessageBuilder.withPayload(payload).build();
            }
        };
    }

}
