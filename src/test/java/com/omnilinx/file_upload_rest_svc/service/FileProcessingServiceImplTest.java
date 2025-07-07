package com.omnilinx.file_upload_rest_svc.service;

import com.omnilinx.file_upload_rest_svc.helper.FileParser;
import com.omnilinx.file_upload_rest_svc.helper.RestClient;
import com.omnilinx.file_upload_rest_svc.model.entity.Player;
import com.omnilinx.file_upload_rest_svc.model.dto.PlayerDto;
import com.omnilinx.file_upload_rest_svc.model.dto.FileDataDto;
import com.omnilinx.file_upload_rest_svc.rabbit.FileMessageProducer;
import com.omnilinx.file_upload_rest_svc.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileProcessingServiceImplTest {

    private static final byte[] MOCK_CONTENT = "name,position,age,team,country\nEmil,GK,27,Strelcha FC,Bulgaria".getBytes();
    private static final String MOCK_FILE_NAME = "players.csv";
    private static final String MOCK_CORRELATION_ID = "test123";


    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private FileParser<PlayerDto> parser;

    @Mock
    private FileMessageProducer fileMessageProducer;

    @Mock
    private RestClient client;

    @InjectMocks
    private FileProcessingServiceImpl toTest;


    @Test
    void testEnqueueFileForProcessingSuccess() {
        FileDataDto dto = buildFileDataDto();
        toTest.enqueueFileForProcessing(dto);
        verify(fileMessageProducer, times(1)).sendFileForProcessing(dto);
    }



    @Test
    void testProcessFileSuccess() {
        FileDataDto fileDataDto = buildFileDataDto();

        PlayerDto dto = new PlayerDto("Emil", "GK", 27, "Strelcha FC", "Bulgaria");
        Player player = new Player(null, "Emil", "GK", 27, "Strelcha FC", "Bulgaria", false);

        when(parser.parse(MOCK_CONTENT)).thenReturn(List.of(dto));

        toTest.processFile(fileDataDto);

        verify(parser).parse(MOCK_CONTENT);
        verify(playerRepository).saveAll(List.of(player));
    }

    private static FileDataDto buildFileDataDto() {
        return FileDataDto.builder()
                .originalFilename(MOCK_FILE_NAME)
                .content(MOCK_CONTENT)
                .correlationId(MOCK_CORRELATION_ID)
                .build();
    }

}