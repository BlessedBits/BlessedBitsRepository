package com.blessedbits.SchoolHub.projections.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class CourseDto {
    private Integer id;
    private String name;
    // To add modules
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private SchoolDto school;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ClassDto> classes;
}
