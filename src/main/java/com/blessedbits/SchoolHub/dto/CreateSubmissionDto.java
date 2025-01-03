package com.blessedbits.SchoolHub.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CreateSubmissionDto {
    private String url;
    private LocalDateTime submittedAt;
}
