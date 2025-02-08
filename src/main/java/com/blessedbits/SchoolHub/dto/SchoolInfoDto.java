package com.blessedbits.SchoolHub.dto;

import lombok.Data;

@Data
public class SchoolInfoDto 
{
    private String name;
    private String address;
    private String phrase;
    private Integer year;
    private String logo;
    private long studentCount;
    private long teacherCount;
}
