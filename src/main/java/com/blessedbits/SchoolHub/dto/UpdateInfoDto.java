package com.blessedbits.SchoolHub.dto;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateInfoDto 
{
    @Email(message = "Invalid email format")
    private String email;
    private String username;   
}
