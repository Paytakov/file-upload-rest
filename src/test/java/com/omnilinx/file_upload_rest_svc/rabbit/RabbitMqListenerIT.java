package com.omnilinx.file_upload_rest_svc.rabbit;

import com.omnilinx.file_upload_rest_svc.model.entity.Player;
import com.omnilinx.file_upload_rest_svc.model.dto.FileUploadResponseDto;
import com.omnilinx.file_upload_rest_svc.repository.PlayerRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class RabbitMqListenerIT {

    @Container
    public static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:4.1");

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("file_db")
            .withUsername("test")
            .withPassword("test");


    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PlayerRepository playerRepository;

    private static final String CSV_CONTENT = """
            name,position,age,team,country
            Gogo,LF,27,Pisa FC,Italy
            Savo,RB,28,Munich FC,Germany
            """;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbit::getHost);
        registry.add("spring.rabbitmq.port", rabbit::getAmqpPort);
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void testFileUploadAndVerifyAsyncProcessing() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(CSV_CONTENT.getBytes(StandardCharsets.UTF_8)) {
            @Override
            public String getFilename() {
                return "test.csv";
            }
        });

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<FileUploadResponseDto> response = restTemplate.postForEntity("/api/v1/files/upload", request, FileUploadResponseDto.class);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());

        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> {
                    List<Player> players = playerRepository.findAll();
                    assertEquals(2, players.size());
                    assertThat(players.get(0).getName()).isEqualTo("Gogo");
                });
    }
}
