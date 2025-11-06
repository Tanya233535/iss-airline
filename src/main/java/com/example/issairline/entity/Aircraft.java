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

    /**
     * Бортовой номер (уникальный идентификатор самолёта)
     * Например: SU123, RA-89012
     */
    @Id
    @Column(name = "aircraft_code", length = 10, nullable = false)
    private String aircraftCode;

    /**
     * Модель самолёта (например, "Airbus A320" или "Boeing 737-800")
     */
    @Column(nullable = false, length = 50)
    private String model;

    /**
     * Год выпуска самолёта
     */
    @Column(name = "manufacture_year")
    private Integer manufactureYear;

    /**
     * Количество пассажирских мест
     */
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

    /**
     * Дата последнего технического обслуживания
     */
    @Column(name = "last_maintenance_date")
    private LocalDate lastMaintenanceDate;

    /**
     * Наработка часов полёта
     */
    @Column(name = "total_flight_hours")
    private Integer totalFlightHours;

    /**
     * Вспомогательное перечисление статусов самолёта
     */
    public enum Status {
        ACTIVE, MAINTENANCE, RETIRED
    }
}

