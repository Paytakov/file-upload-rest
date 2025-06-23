package com.omnilinx.file_upload_rest_svc.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PlayerDto {

    private String name;
    private String position;
    private int age;
    private String team;
    private String country;

}
