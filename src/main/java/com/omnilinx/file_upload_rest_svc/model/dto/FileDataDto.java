package com.omnilinx.file_upload_rest_svc.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileDataDto {

    @NotBlank
    private String originalFilename;
    @NotBlank
    private String contentType;
    @NotBlank
    private long size;
    @NotNull
    private byte[] content;
    private String correlationId;
}
