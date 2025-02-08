package com.blessedbits.SchoolHub.dto;
import jakarta.validation.constraints.Email;

import lombok.Data;

@Data
public class RegisterDto {
    private String firstName;
    private String lastName;
    private String username;
    @Email(message = "Invalid email format")
    private String email;
    private String password;
}
