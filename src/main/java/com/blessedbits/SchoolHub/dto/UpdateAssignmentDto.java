package com.blessedbits.SchoolHub.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UpdateAssignmentDto {
    private String title;
    private String description;
    private String url;
    private LocalDateTime dueDate;
}
