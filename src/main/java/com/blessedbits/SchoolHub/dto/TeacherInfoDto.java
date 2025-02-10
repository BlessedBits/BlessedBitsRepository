package com.blessedbits.SchoolHub.dto;

import lombok.Data;

@Data
public class TeacherInfoDto {
    private Integer id;
    private String firstName;
    private String lastName;
    private String profileImage;
    private String duty;
    private String role;
}