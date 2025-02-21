package com.blessedbits.SchoolHub.dto;

import lombok.Data;
import java.time.LocalDateTime;

import com.blessedbits.SchoolHub.misc.GradeType;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
public class GradeDto {
    private Integer grade;
    private GradeType type;
    @JsonInclude(JsonInclude.Include.NON_NULL) 
    private LocalDateTime gradedAt;
    @JsonInclude(JsonInclude.Include.NON_NULL) 
    private Integer studentId;
    @JsonInclude(JsonInclude.Include.NON_NULL) 
    private Integer courseId;
}

