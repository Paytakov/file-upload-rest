package com.omnilinx.file_upload_rest_svc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class FileUploadRestSvcApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileUploadRestSvcApplication.class, args);
	}

}
