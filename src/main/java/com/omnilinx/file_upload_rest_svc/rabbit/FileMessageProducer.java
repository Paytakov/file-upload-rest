package com.omnilinx.file_upload_rest_svc.rabbit;

import com.omnilinx.file_upload_rest_svc.model.dto.FileDataDto;

public interface FileMessageProducer {

    void sendFileForProcessing(FileDataDto dto);
}
