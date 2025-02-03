package com.blessedbits.SchoolHub.dto;

import lombok.Data;

@Data
public class CreateSchoolDto {
    private String name;
    private String address;
    private Integer year;
}
