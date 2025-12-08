package com.example.issairline.api.mapper;

import com.example.issairline.api.dto.CrewMemberDto;
import com.example.issairline.entity.CrewMember;
import com.example.issairline.entity.Flight;

public class CrewMemberMapper {

    public static CrewMemberDto toDto(CrewMember c) {
        CrewMemberDto dto = new CrewMemberDto();
        dto.setMemberId(c.getMemberId());
        dto.setLastName(c.getLastName());
        dto.setFirstName(c.getFirstName());
        dto.setMiddleName(c.getMiddleName());
        dto.setRole(c.getRole());
        dto.setQualification(c.getQualification());
        dto.setExperienceYears(c.getExperienceYears());
        dto.setFlightId(c.getFlight() == null ? null : c.getFlight().getId());
        return dto;
    }

    public static CrewMember toEntity(CrewMemberDto dto, Flight flight) {
        CrewMember c = new CrewMember();
        c.setMemberId(dto.getMemberId());
        c.setLastName(dto.getLastName());
        c.setFirstName(dto.getFirstName());
        c.setMiddleName(dto.getMiddleName());
        c.setRole(dto.getRole());
        c.setQualification(dto.getQualification());
        c.setExperienceYears(dto.getExperienceYears());
        c.setFlight(flight);
        return c;
    }
}
