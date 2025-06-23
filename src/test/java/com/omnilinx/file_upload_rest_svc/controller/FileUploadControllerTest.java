package com.omnilinx.file_upload_rest_svc.controller;

import com.omnilinx.file_upload_rest_svc.service.FileProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@WebMvcTest(FileUploadController.class)
class FileUploadControllerTest {

    private static final String API_ENDPOINT = "/api/v1/files/upload";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FileProcessingService fileProcessingService;

    private MockMultipartFile testCsvFile;

    @BeforeEach
    void setUp() {
        testCsvFile = new MockMultipartFile(
                "file",
                "players.csv",
                "text/csv",
                "name,position,age,team,country\nTommy,ST,25,Test FC,Testland".getBytes()
        );
    }

    @Test
    void testUploadFileSuccess() throws Exception {
        mockMvc.perform(multipart(API_ENDPOINT).file(testCsvFile))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value("File upload accepted."))
                .andExpect(jsonPath("$.correlationId").exists())
                .andExpect(jsonPath("$.correlationId").isNotEmpty());
    }

    @Test
    void testUploadFileWithWhenEmptyShouldReturnBadRequestResponse() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "players.csv", "text/csv", new byte[0]);
        mockMvc.perform(multipart(API_ENDPOINT).file(emptyFile))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Empty file is not allowed."));
    }

    @Test
    void testUploadFileWhenInvalidExtensionShouldReturnBadRequestResponse() throws Exception {
        MockMultipartFile invalidFile = new MockMultipartFile("file", "players.txt", "text/plain", "data".getBytes());
        mockMvc.perform(multipart(API_ENDPOINT).file(invalidFile))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Only .csv files are supported."));
    }

    @Test
    void testUploadFile_ioException() throws Exception {
       // TODO:
    }

    @Test
    void testUploadFileShouldReturnInternalServerResponse() throws Exception {
        doThrow(new RuntimeException("fail")).when(fileProcessingService).enqueueFileForProcessing(any());

        mockMvc.perform(multipart(API_ENDPOINT).file(testCsvFile))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to process file."));
    }
}