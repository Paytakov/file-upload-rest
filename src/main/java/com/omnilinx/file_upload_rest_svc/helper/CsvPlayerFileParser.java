package com.omnilinx.file_upload_rest_svc.helper;

import com.omnilinx.file_upload_rest_svc.model.PlayerDto;
import com.omnilinx.file_upload_rest_svc.util.ModelFactoryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class CsvPlayerFileParser implements FileParser<PlayerDto> {


    @Override
    public List<PlayerDto> parse(byte[] content) {
        List<PlayerDto> playerDtos = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader((new ByteArrayInputStream(content)), StandardCharsets.UTF_8))) {

            String line;
            boolean headerSkipped = false;

            while ((line = reader.readLine()) != null) {

                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }

                String[] tokens = line.split(",");
                if (tokens.length != 5) {
                    log.warn("Skipping invalid line: '{}'", line);
                    continue;
                }

                PlayerDto playerDto = parseData(tokens);
                if (playerDto != null) {
                    playerDtos.add(playerDto);
                }
            }

        } catch (Exception e) {
            log.error("Error parsing CSV file.", e);
            throw new RuntimeException("Failed to parse CSV file.", e);
        }

        return playerDtos;

    }

    private PlayerDto parseData(String[] tokens) {
        PlayerDto dto = null;
        try {
            String name = tokens[0].trim();
            String position = tokens[1].trim();
            int age = Integer.parseInt(tokens[2].trim());
            String team = tokens[3].trim();
            String country = tokens[4].trim();

            dto = ModelFactoryUtil.buildPlayerDto(name, position, age, team, country);

        } catch (NumberFormatException ex) {
            log.warn("Skipping line due to number format issue.", ex);
        }
        return dto;
    }
}
