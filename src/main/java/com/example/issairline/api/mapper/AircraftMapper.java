package com.example.issairline.api.mapper;

import com.example.issairline.api.dto.AircraftDto;
import com.example.issairline.entity.Aircraft;

public class AircraftMapper {

    public static AircraftDto toDto(Aircraft a) {
        AircraftDto dto = new AircraftDto();
        dto.setAircraftCode(a.getAircraftCode());
        dto.setModel(a.getModel());
        dto.setManufactureYear(a.getManufactureYear());
        dto.setCapacity(a.getCapacity());
        dto.setStatus(a.getStatus().name());
        dto.setLastMaintenanceDate(a.getLastMaintenanceDate() == null ? null : a.getLastMaintenanceDate().toString());
        dto.setTotalFlightHours(a.getTotalFlightHours());
        return dto;
    }

    public static Aircraft toEntity(AircraftDto dto) {
        Aircraft a = new Aircraft();
        a.setAircraftCode(dto.getAircraftCode());
        a.setModel(dto.getModel());
        a.setManufactureYear(dto.getManufactureYear());
        a.setCapacity(dto.getCapacity());
        a.setStatus(Aircraft.Status.valueOf(dto.getStatus()));
        a.setLastMaintenanceDate(dto.getLastMaintenanceDate() == null ? null : java.time.LocalDate.parse(dto.getLastMaintenanceDate()));
        a.setTotalFlightHours(dto.getTotalFlightHours());
        return a;
    }
}
