package com.example.issairline.api.dto;

import lombok.Data;

@Data
public class AircraftDto {

    private String aircraftCode;
    private String model;
    private Integer manufactureYear;
    private Integer capacity;
    private String status;
    private String lastMaintenanceDate;
    private Double totalFlightHours;
}
