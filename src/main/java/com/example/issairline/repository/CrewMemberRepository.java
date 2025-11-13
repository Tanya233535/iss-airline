package com.example.issairline.repository;

import com.example.issairline.entity.CrewMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrewMemberRepository extends JpaRepository<CrewMember, Long> {
    List<CrewMember> findByFlight_Id(Long flightId);
}
