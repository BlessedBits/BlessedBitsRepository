package com.blessedbits.SchoolHub.projections.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class ClassDto {
    private Integer id;
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer homeroomTeacherId; // In case no additional info is needed
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserDto homeroomTeacher;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer schoolId; // In case no additional info is needed
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private SchoolDto school;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<UserDto> students;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<CourseDto> courses;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ScheduleDto> schedules;
}
