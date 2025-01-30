package com.blessedbits.SchoolHub.dto;
import lombok.Data;

@Data
public class UserProfileDto {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String role;
    private String duty;
    private String profileImage;
    private String school;
}
