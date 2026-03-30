package com.health.care_management.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserDto {
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String contactNumber;

    // Getters and Setters
}
