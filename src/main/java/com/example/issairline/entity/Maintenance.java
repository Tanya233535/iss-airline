package com.example.issairline.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "maintenance")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aircraft_code", nullable = false)
    @NotNull(message = "Необходимо выбрать самолёт")
    private Aircraft aircraft;

    @Column(name = "maintenance_date", nullable = false)
    @NotNull(message = "Укажите дату обслуживания")
    private LocalDate maintenanceDate;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "Тип проверки обязателен")
    private String type; // A-check, B-check, C-check, D-check

    @Column(name = "engineer_name", nullable = false, length = 100)
    @NotBlank(message = "Введите имя инженера")
    private String engineerName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "next_due_date")
    private LocalDate nextDueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.SCHEDULED;

    public enum Status {
        COMPLETED,
        IN_PROGRESS,
        SCHEDULED
    }
}
