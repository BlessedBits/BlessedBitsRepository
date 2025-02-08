package com.blessedbits.SchoolHub.projections.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class ModuleDto {
    private Long id;
    private String name;
    private boolean isVisible = true;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer courseId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private CourseDto course;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<MaterialDto> materials;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<AssignmentDto> assignments;
}
