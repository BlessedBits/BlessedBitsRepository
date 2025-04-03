package com.blessedbits.SchoolHub.dto;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class RegisterDto {
    @NotBlank(message = "First name cannot be empty")
    private String firstName;

    @NotBlank(message = "Last name cannot be empty")
    private String lastName;
}
