package com.blessedbits.SchoolHub.projections.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class AchievementDto {
    private Integer id;
    private String title;
    private String description;
    private String image;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer schoolId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private SchoolDto school;
}
