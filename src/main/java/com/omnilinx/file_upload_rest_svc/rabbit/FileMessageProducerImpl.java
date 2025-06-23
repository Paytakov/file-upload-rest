package com.omnilinx.file_upload_rest_svc.rabbit;

import com.omnilinx.file_upload_rest_svc.model.dto.FileDataDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FileMessageProducerImpl implements FileMessageProducer {

    private final RabbitTemplate template;

    public FileMessageProducerImpl(RabbitTemplate template) {
        this.template = template;
    }


    @Override
    public void sendFileForProcessing(FileDataDto dto) {
        log.info("Sending file '{}' to file.processing-queue", dto.getOriginalFilename());
        template.convertAndSend(
                RabbitMQConstants.EXCHANGE_NAME,
                RabbitMQConstants.ROUTING_KEY,
                dto
        );
    }
}
