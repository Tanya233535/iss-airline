package com.example.issairline.api.dto;

import lombok.Data;

@Data
public class CrewMemberDto {

    private Long memberId;

    private String lastName;
    private String firstName;
    private String middleName;

    private String role;
    private String qualification;
    private Integer experienceYears;

    private Long flightId;
}
