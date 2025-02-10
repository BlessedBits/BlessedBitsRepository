package com.blessedbits.SchoolHub.dto;

import lombok.Data;
import java.util.Set;

@Data
public class CreateCourseDto {
    private String name;
    private Integer schoolId;
    private Set<Integer> teacherIds;
}
