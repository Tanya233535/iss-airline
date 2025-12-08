package com.example.issairline.api.mapper;

import com.example.issairline.api.dto.PassengerDto;
import com.example.issairline.entity.Flight;
import com.example.issairline.entity.Passenger;

public class PassengerMapper {

    public static PassengerDto toDto(Passenger p) {
        PassengerDto dto = new PassengerDto();
        dto.setId(p.getId());
        dto.setLastName(p.getLastName());
        dto.setFirstName(p.getFirstName());
        dto.setMiddleName(p.getMiddleName());
        dto.setPassportNumber(p.getPassportNumber());
        dto.setTicketNumber(p.getTicketNumber());
        dto.setSeat(p.getSeat());
        dto.setFlightId(p.getFlight() == null ? null : p.getFlight().getId());
        return dto;
    }

    public static Passenger toEntity(PassengerDto dto, Flight flight) {
        Passenger p = new Passenger();
        p.setId(dto.getId());
        p.setLastName(dto.getLastName());
        p.setFirstName(dto.getFirstName());
        p.setMiddleName(dto.getMiddleName());
        p.setPassportNumber(dto.getPassportNumber());
        p.setTicketNumber(dto.getTicketNumber());
        p.setSeat(dto.getSeat());
        p.setFlight(flight);
        return p;
    }
}
