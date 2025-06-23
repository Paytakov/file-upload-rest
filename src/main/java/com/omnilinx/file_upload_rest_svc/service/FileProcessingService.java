package com.omnilinx.file_upload_rest_svc.service;

import com.omnilinx.file_upload_rest_svc.model.dto.FileDataDto;

public interface FileProcessingService {

     void enqueueFileForProcessing(FileDataDto dto);
}
