package com.blessedbits.SchoolHub.projections.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class SchoolContactsDto {
    private Integer id;
    private String phoneNumber;
    private String email;
    private String youtubeLink;
    private String facebookLink;
    private String instagramLink;
    private String tiktokLink;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer schoolId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private SchoolDto school;
}
