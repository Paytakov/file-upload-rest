package com.omnilinx.file_upload_rest_svc.config;

import com.omnilinx.file_upload_rest_svc.rabbit.RabbitMQConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
@ComponentScan(basePackages = "com.omnilinx.file_upload_rest_svc.service")
public class RabbitConfiguration {


    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(RabbitMQConstants.EXCHANGE_NAME);
    }

    @Bean
    public Queue queue() {
        return new Queue(RabbitMQConstants.QUEUE_NAME);
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(RabbitMQConstants.ROUTING_KEY);
    }
}
