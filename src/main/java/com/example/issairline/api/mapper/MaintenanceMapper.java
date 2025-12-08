package com.example.issairline.api.mapper;

import com.example.issairline.api.dto.MaintenanceDto;
import com.example.issairline.entity.Aircraft;
import com.example.issairline.entity.Maintenance;

import java.time.LocalDate;

public class MaintenanceMapper {

    public static MaintenanceDto toDto(Maintenance m) {
        MaintenanceDto dto = new MaintenanceDto();
        dto.setId(m.getId());
        dto.setAircraftCode(m.getAircraft().getAircraftCode());
        dto.setMaintenanceDate(m.getMaintenanceDate().toString());
        dto.setType(m.getType());
        dto.setEngineerName(m.getEngineerName());
        dto.setDescription(m.getDescription());
        dto.setNextDueDate(m.getNextDueDate() == null ? null : m.getNextDueDate().toString());
        dto.setStatus(m.getStatus().name());
        return dto;
    }

    public static Maintenance toEntity(MaintenanceDto dto, Aircraft aircraft) {
        Maintenance m = new Maintenance();
        m.setId(dto.getId());
        m.setAircraft(aircraft);
        m.setMaintenanceDate(LocalDate.parse(dto.getMaintenanceDate()));
        m.setType(dto.getType());
        m.setEngineerName(dto.getEngineerName());
        m.setDescription(dto.getDescription());
        m.setNextDueDate(dto.getNextDueDate() == null ? null : LocalDate.parse(dto.getNextDueDate()));
        m.setStatus(Maintenance.Status.valueOf(dto.getStatus()));
        return m;
    }
}
