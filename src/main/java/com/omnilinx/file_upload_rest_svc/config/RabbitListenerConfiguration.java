package com.omnilinx.file_upload_rest_svc.config;

import com.omnilinx.file_upload_rest_svc.rabbit.RabbitMQConstants;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitListenerConfiguration {

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);

        // TODO: Add the failed message to DLM? -> read more about the topic
        factory.setAdviceChain(
                RetryInterceptorBuilder.stateless()
                        .maxAttempts(RabbitMQConstants.MAX_ATTEMPTS_COUNT)
                        .backOffOptions(
                                RabbitMQConstants.BACKOFF_INITIAL_INTERVAL,
                                RabbitMQConstants.BACKOFF_MULTIPLIER,
                                RabbitMQConstants.BACKOFF_MAX_INTERVAL)
                        .recoverer(new RejectAndDontRequeueRecoverer())
                        .build()
        );

        return factory;
    }
}
