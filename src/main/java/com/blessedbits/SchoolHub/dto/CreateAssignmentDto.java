package com.blessedbits.SchoolHub.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CreateAssignmentDto {
    private String title;
    private String description;
    private String url;
    private LocalDateTime dueDate;
    private Long moduleId;
}
