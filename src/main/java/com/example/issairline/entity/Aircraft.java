package com.example.issairline.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Сущность "Самолёт"
 * Таблица aircrafts — хранит информацию о воздушных судах авиакомпании
 */
@Entity
@Table(name = "aircrafts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Aircraft {

    @Id
    @Column(name = "aircraft_code", length = 10, nullable = false)
    private String aircraftCode;

    @Column(nullable = false, length = 50)
    private String model;

    @Column(name = "manufacture_year")
    private Integer manufactureYear;

    @Column(nullable = false)
    private Integer capacity;

    /**
     * Текущее состояние самолёта:
     * ACTIVE — используется
     * MAINTENANCE — на техобслуживании
     * RETIRED — списан
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Status status;

    @Column(name = "last_maintenance_date")
    private LocalDate lastMaintenanceDate;

    @Column(name = "total_flight_hours")
    private Integer totalFlightHours;

    public enum Status {
        ACTIVE, MAINTENANCE, RETIRED
    }
}

