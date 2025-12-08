package com.example.issairline.api.dto;

import lombok.Data;

@Data
public class PassengerDto {

    private Long id;

    private String lastName;
    private String firstName;
    private String middleName;

    private String passportNumber;
    private String ticketNumber;
    private String seat;

    private Long flightId;
}
