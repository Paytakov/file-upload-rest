package com.omnilinx.file_upload_rest_svc.service;

import com.omnilinx.file_upload_rest_svc.rabbit.RabbitMQConstants;
import com.omnilinx.file_upload_rest_svc.model.entity.Player;
import com.omnilinx.file_upload_rest_svc.model.dto.PlayerDto;
import com.omnilinx.file_upload_rest_svc.model.dto.FileDataDto;
import com.omnilinx.file_upload_rest_svc.repository.PlayerRepository;
import com.omnilinx.file_upload_rest_svc.rabbit.FileMessageProducer;
import com.omnilinx.file_upload_rest_svc.helper.FileParser;
import com.omnilinx.file_upload_rest_svc.util.ModelFactoryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FileProcessingServiceImpl implements FileProcessingService {

    private final PlayerRepository playerRepository;
    private final FileParser<PlayerDto> parser;
    private final FileMessageProducer fileMessageProducer;

    public FileProcessingServiceImpl(PlayerRepository playerRepository,
                                     FileParser<PlayerDto> parser,
                                     FileMessageProducer fileMessageProducer) {
        this.playerRepository = playerRepository;
        this.parser = parser;
        this.fileMessageProducer = fileMessageProducer;
    }


    @Override
    public void enqueueFileForProcessing(FileDataDto fileDataDto) {
        log.info("File {} is ready to be enqueued.", fileDataDto.getOriginalFilename());
        fileMessageProducer.sendFileForProcessing(fileDataDto);
    }

    @RabbitListener(queues = RabbitMQConstants.QUEUE_NAME)
    void processFile(FileDataDto fileDataDto) {
        log.info("Received file for processing.");
        log.info("Processing file, correlationId= {}", fileDataDto.getCorrelationId());
        List<PlayerDto> playerDtoList = parser.parse(fileDataDto.getContent());
        List<Player> players = playerDtoList.stream()
                .map(dto -> ModelFactoryUtil.buildPlayer(
                        dto.getName(),
                        dto.getPosition(),
                        dto.getAge(),
                        dto.getTeam(),
                        dto.getCountry()))
                .toList();

        log.info("Storing the players data.");
        playerRepository.saveAll(players);
    }

}
