package com.blessedbits.SchoolHub.projections.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class MaterialDto {
    private Long id;
    private String title;
    private String description;
    private String url;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long moduleId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ModuleDto module;
}
