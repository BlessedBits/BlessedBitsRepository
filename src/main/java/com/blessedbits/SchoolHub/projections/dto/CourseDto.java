package com.blessedbits.SchoolHub.projections.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class CourseDto {
    private Integer id;
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer schoolId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private SchoolDto school;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ModuleDto> modules;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ClassDto> classes;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<UserDto> teachers;
}
