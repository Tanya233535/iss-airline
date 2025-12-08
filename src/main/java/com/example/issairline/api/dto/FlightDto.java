package com.example.issairline.api.dto;

import lombok.Data;

@Data
public class FlightDto {

    private Long id;

    private String flightNo;

    private String scheduledDeparture;
    private String scheduledArrival;

    private String departureAirport;
    private String arrivalAirport;

    private String actualDeparture;
    private String actualArrival;

    private String status;

    private String aircraftCode;

    private Integer passengerCount;

    private String routeDuration;
}
