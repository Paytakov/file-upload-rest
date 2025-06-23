package com.omnilinx.file_upload_rest_svc.controller;

import com.omnilinx.file_upload_rest_svc.model.dto.FileDataDto;
import com.omnilinx.file_upload_rest_svc.model.dto.FileUploadResponseDto;
import com.omnilinx.file_upload_rest_svc.service.FileProcessingService;
import com.omnilinx.file_upload_rest_svc.util.ModelFactoryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("api/v1/files")
@Slf4j
public class FileUploadController {

    private final FileProcessingService fileProcessingService;

    public FileUploadController(FileProcessingService fileProcessingService) {
        this.fileProcessingService = fileProcessingService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Empty file is not allowed.");
        }

        if (!Objects.requireNonNull(filename).endsWith(".csv")) {
            return ResponseEntity.badRequest().body("Only .csv files are supported.");
        }


        try {
            byte[] fileContent = readFileBytes(file);
            FileDataDto fileDto = FileDataDto.builder()
                    .originalFilename(filename)
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .content(fileContent)
                    .build();

            FileUploadResponseDto response = ModelFactoryUtil.buildFileUploadResponse();
            fileDto.setCorrelationId(response.getCorrelationId());
            fileProcessingService.enqueueFileForProcessing(fileDto);
            log.info("File {} is uploading. Processing will happen asynchronously.", fileDto.getOriginalFilename());
            return ResponseEntity.accepted().body(response);
        } catch (IOException e) {
            log.error("Failed to read file bytes.", e);
            return ResponseEntity.badRequest().body("Failed to read file content.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to process file.");
        }
    }

    protected byte[] readFileBytes(MultipartFile file) throws IOException {
        return file.getBytes();
    }
}
