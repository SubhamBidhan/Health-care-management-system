package com.health.care_management.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DoctorDTO {
    private String doctorUserName;
    private String doctorPassword;
    private String doctorName;
    private String doctorExperience;
    private String doctorSpecialization;
    private String doctorPhone;
    private int slots;

  
}
