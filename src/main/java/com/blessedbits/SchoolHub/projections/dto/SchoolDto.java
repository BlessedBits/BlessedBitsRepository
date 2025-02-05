package com.blessedbits.SchoolHub.projections.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class SchoolDto {
    private Integer id;
    private String name;
    private String address;
    private String logo;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ClassDto> classes;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<CourseDto> courses;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<UserDto> users;
    // Todo add news
}
