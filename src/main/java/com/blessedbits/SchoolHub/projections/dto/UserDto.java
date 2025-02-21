package com.blessedbits.SchoolHub.projections.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private Integer id;
    private String username;
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
}
