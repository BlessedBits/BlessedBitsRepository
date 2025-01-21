package com.blessedbits.SchoolHub.dto;
import lombok.Data;

@Data
public class CreateNewsDTO 
{
    private String title;
    
    private String category;

    private String link;

    private Integer schoolId;
}
