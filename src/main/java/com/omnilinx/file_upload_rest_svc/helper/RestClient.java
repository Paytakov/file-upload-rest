package com.omnilinx.file_upload_rest_svc.helper;

import com.omnilinx.file_upload_rest_svc.exception.PlayerSendingException;
import com.omnilinx.file_upload_rest_svc.model.dto.PlayerDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Slf4j
public class RestClient {

    private final RestTemplate restTemplate;
    private final RestCallTimeIntervalDispatcher timeIntervalDispatcher;
    private final String url;


    public RestClient(RestTemplate restTemplate,
                      RestCallTimeIntervalDispatcher timeIntervalDispatcher,
                      @Value("${mock.server.api.url}") String url) {
        this.restTemplate = restTemplate;
        this.timeIntervalDispatcher = timeIntervalDispatcher;
        this.url = url;
    }

    public void send(List<PlayerDto> players) {
        if (timeIntervalDispatcher.isWithinAllowedTimeInterval()) {
            try {
                log.info("Sending players info to mock server: {}", url);
                restTemplate.postForEntity(url, players, Void.class);
            } catch (Exception e) {
                 log.error("Failed to send players data to mock server", e);
                 throw new PlayerSendingException("REST call to mock server failed", e);
            }
        } else {
            log.warn("Skipping sending data to remote server — current time is outside allowed time window.");
        }

    }

}
