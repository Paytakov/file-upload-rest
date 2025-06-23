package com.omnilinx.file_upload_rest_svc.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class FileUploadResponseDto {

    private String message;
    private String correlationId;
}
