package com.omnilinx.file_upload_rest_svc.helper;

import com.omnilinx.file_upload_rest_svc.model.PlayerDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestClientTest {

    private static final String TEST_URL = "http://test.url/api";
    private static final List<PlayerDto> PLAYER_DTO_LIST = getPlayerDtos();

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private RestCallTimeIntervalDispatcher dispatcher;

    private RestClient toTest;

    @BeforeEach
    void setUp() {
        toTest = new RestClient(restTemplate, dispatcher, TEST_URL);
    }


    @Test
    void testSendShouldPostWhenInAllowedTimeInterval() {
        when(dispatcher.isWithinAllowedTimeInterval()).thenReturn(true);
        toTest.send(PLAYER_DTO_LIST);
        verify(restTemplate).postForEntity(eq(TEST_URL), eq(PLAYER_DTO_LIST), eq(Void.class));
    }


    @Test
    void testSendShouldSkipWhenTimeIntervalIsNotAllowed() {
        when(dispatcher.isWithinAllowedTimeInterval()).thenReturn(false);
        toTest.send(PLAYER_DTO_LIST);
        verifyNoInteractions(restTemplate);
    }

    @Test
    void testSendShouldThrow() {
        when(dispatcher.isWithinAllowedTimeInterval()).thenReturn(true);
        when(restTemplate.postForEntity(eq(TEST_URL), eq(PLAYER_DTO_LIST), eq(Void.class)))
                .thenThrow(new RestClientException("Simulated error"));

        assertThrows(RuntimeException.class, () -> toTest.send(PLAYER_DTO_LIST));
        verify(restTemplate).postForEntity(eq(TEST_URL), eq(PLAYER_DTO_LIST), eq(Void.class));
    }

    private static List<PlayerDto> getPlayerDtos() {
        return List.of(new PlayerDto("Sam", "GK", 27, "Test FC", "Test"));
    }
}