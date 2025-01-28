package com.blessedbits.SchoolHub.dto;

import lombok.Data;

@Data
public class CreateClassDto {
    private String name;
    private String homeroomTeacher;
    private Integer schoolId;
}
