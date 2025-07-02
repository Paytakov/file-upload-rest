package com.omnilinx.file_upload_rest_svc.util;

import com.omnilinx.file_upload_rest_svc.model.entity.Player;
import com.omnilinx.file_upload_rest_svc.model.dto.PlayerDto;
import com.omnilinx.file_upload_rest_svc.model.dto.FileUploadResponseDto;
import java.util.UUID;

public class ModelFactoryUtil {

    private static final String RESPONSE_MESSAGE = "File upload accepted.";
    private static final String CORRELATION_ID = UUID.randomUUID().toString();


    public static FileUploadResponseDto buildFileUploadResponse() {
        return FileUploadResponseDto
                .builder()
                .message(RESPONSE_MESSAGE)
                .correlationId(CORRELATION_ID)
                .build();
    }

    // TODO: Make the method generic
    public static PlayerDto buildPlayerDto(String name, String position, int age, String team, String country) {
        return PlayerDto.builder()
                .name(name)
                .position(position)
                .age(age)
                .team(team)
                .country(country)
                .build();
    }

    public static Player buildPlayer(String name, String position, int age, String team, String country) {
        return Player.builder()
                .name(name)
                .position(position)
                .age(age)
                .team(team)
                .country(country)
                .build();
    }
}
