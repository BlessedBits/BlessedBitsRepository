package com.blessedbits.SchoolHub.projections.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubmissionDto {
    private Long id;
    private String url;
    private LocalDateTime submittedAt;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer studentId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserDto student;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer teacherId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserDto teacher;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long assignmentId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private AssignmentDto assignment;
}
