package com.omnilinx.file_upload_rest_svc.rabbit;

import com.omnilinx.file_upload_rest_svc.model.dto.FileDataDto;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class FileMessageProducerImplTest {

    private final RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
    private final FileMessageProducerImpl producer = new FileMessageProducerImpl(rabbitTemplate);

    @Test
    void sendFileForProcessing_shouldDelegateToRabbitTemplate() {
        FileDataDto dto = FileDataDto.builder()
                .originalFilename("players.csv")
                .contentType("text/csv")
                .size(123L)
                .correlationId("test-corr-id")
                .content(new byte[]{1, 2, 3})
                .build();

        producer.sendFileForProcessing(dto);

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq(RabbitMQConstants.EXCHANGE_NAME),
                eq(RabbitMQConstants.ROUTING_KEY),
                eq(dto)
        );
    }

}