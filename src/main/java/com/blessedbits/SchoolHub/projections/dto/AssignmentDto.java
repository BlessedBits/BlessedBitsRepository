package com.blessedbits.SchoolHub.projections.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AssignmentDto {
    private Long id;
    private String title;
    private String description;
    private String url;
    private LocalDateTime dueDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long moduleId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ModuleDto module;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<SubmissionDto> submissions;
}
