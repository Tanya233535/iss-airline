package com.example.issairline.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
    @NotBlank(message = "Код самолёта обязателен")
    @Size(max = 10, message = "Код самолёта не должен превышать 10 символов")
    @Column(name = "aircraft_code", length = 10, nullable = false)
    private String aircraftCode;

    @NotBlank(message = "Модель обязательна")
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String model;

    @Min(value = 1930, message = "Год выпуска должен быть не ранее 1930")
    @Max(value = 2025, message = "Год выпуска не может быть больше 2025")
    @Column(name = "manufacture_year")
    private Integer manufactureYear;

    @NotNull(message = "Вместимость обязательна")
    @Min(value = 1, message = "Вместимость должна быть больше 0")
    @Max(value = 500, message = "Вместимость не может превышать 500 мест")
    @Column(nullable = false)
    private Integer capacity;

    @NotNull(message = "Статус обязателен")
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Status status;

    @PastOrPresent(message = "Дата последнего ТО не может быть в будущем")
    @Column(name = "last_maintenance_date")
    private LocalDate lastMaintenanceDate;

    @DecimalMin(value = "0.0", inclusive = true, message = "Налёт не может быть отрицательным")
    @Column(name = "total_flight_hours")
    private Double totalFlightHours = 0.0;

    public enum Status {
        ACTIVE, MAINTENANCE, RETIRED
    }

    @Transient
    public String getFormattedFlightHours() {
        if (totalFlightHours == null) return "0 ч 00 мин";
        int hours = totalFlightHours.intValue();
        int minutes = (int) Math.round((totalFlightHours - hours) * 60);
        return String.format("%d ч %02d мин", hours, minutes);
    }
}
