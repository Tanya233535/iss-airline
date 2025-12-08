package com.example.issairline.api.mapper;

import com.example.issairline.api.dto.FlightDto;
import com.example.issairline.entity.Aircraft;
import com.example.issairline.entity.Flight;

import java.time.LocalDateTime;

public class FlightMapper {

    public static FlightDto toDto(Flight f) {
        FlightDto dto = new FlightDto();
        dto.setId(f.getId());
        dto.setFlightNo(f.getFlightNo());
        dto.setScheduledDeparture(f.getScheduledDeparture().toString());
        dto.setScheduledArrival(f.getScheduledArrival().toString());
        dto.setDepartureAirport(f.getDepartureAirport());
        dto.setArrivalAirport(f.getArrivalAirport());
        dto.setActualDeparture(f.getActualDeparture() == null ? null : f.getActualDeparture().toString());
        dto.setActualArrival(f.getActualArrival() == null ? null : f.getActualArrival().toString());
        dto.setStatus(f.getStatus().name());
        dto.setAircraftCode(f.getAircraft().getAircraftCode());
        dto.setPassengerCount(f.getPassengerCount());
        dto.setRouteDuration(f.getRouteDuration());
        return dto;
    }

    public static Flight toEntity(FlightDto dto, Aircraft aircraft) {
        Flight f = new Flight();
        f.setId(dto.getId());
        f.setFlightNo(dto.getFlightNo());
        f.setScheduledDeparture(LocalDateTime.parse(dto.getScheduledDeparture()));
        f.setScheduledArrival(LocalDateTime.parse(dto.getScheduledArrival()));
        f.setDepartureAirport(dto.getDepartureAirport());
        f.setArrivalAirport(dto.getArrivalAirport());
        f.setActualDeparture(dto.getActualDeparture() == null ? null : LocalDateTime.parse(dto.getActualDeparture()));
        f.setActualArrival(dto.getActualArrival() == null ? null : LocalDateTime.parse(dto.getActualArrival()));
        f.setStatus(dto.getStatus() == null ? Flight.Status.SCHEDULED : Flight.Status.valueOf(dto.getStatus()));
        f.setAircraft(aircraft);
        f.setPassengerCount(dto.getPassengerCount() == null ? 0 : dto.getPassengerCount());
        return f;
    }
}
