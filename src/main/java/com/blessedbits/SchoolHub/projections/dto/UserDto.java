package com.blessedbits.SchoolHub.projections.dto;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;


@Data
public class UserDto {
    private Integer id;
    private String username;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private boolean isConfirmed;
    private String profileImage;
    private String duty;
    private String role;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer userClassId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ClassDto userClass;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer schoolId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private SchoolDto school;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<CourseDto> courses;
}
