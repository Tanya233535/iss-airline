package com.example.issairline.api.dto;

import lombok.Data;

@Data
public class MaintenanceDto {

    private Long id;

    private String aircraftCode;

    private String maintenanceDate;

    private String type;

    private String engineerName;

    private String description;

    private String nextDueDate;

    private String status;
}
