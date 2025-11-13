package com.example.issairline.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "flights")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flight_id")
    private Long id;

    @NotBlank(message = "Номер рейса обязателен")
    @Size(max = 10, message = "Номер рейса не должен превышать 10 символов")
    @Column(name = "flight_no", nullable = false, length = 10)
    private String flightNo;

    @NotNull(message = "Дата и время вылета обязательны")
    @FutureOrPresent(message = "Время вылета не может быть в прошлом")
    @Column(name = "scheduled_departure", nullable = false)
    private LocalDateTime scheduledDeparture;

    @NotNull(message = "Дата и время прибытия обязательны")
    @Column(name = "scheduled_arrival", nullable = false)
    private LocalDateTime scheduledArrival;

    @NotBlank(message = "Аэропорт вылета обязателен")
    @Size(max = 50, message = "Название аэропорта вылета не должно превышать 50 символов")
    @Column(name = "departure_airport", nullable = false, length = 50)
    private String departureAirport;

    @NotBlank(message = "Аэропорт прибытия обязателен")
    @Size(max = 50, message = "Название аэропорта прибытия не должно превышать 50 символов")
    @Column(name = "arrival_airport", nullable = false, length = 50)
    private String arrivalAirport;

    @Column(name = "actual_departure")
    private LocalDateTime actualDeparture;

    @Column(name = "actual_arrival")
    private LocalDateTime actualArrival;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Status status = Status.SCHEDULED;

    @ManyToOne
    @JoinColumn(name = "aircraft_code", nullable = false)
    @NotNull(message = "Самолёт обязателен")
    private Aircraft aircraft;

    @Transient
    private String routeDuration;

    public String getRouteDuration() {
        if (scheduledArrival != null && scheduledDeparture != null) {
            long minutes = java.time.Duration.between(scheduledDeparture, scheduledArrival).toMinutes();
            long hours = minutes / 60;
            long mins = minutes % 60;
            return hours + " ч " + mins + " мин";
        }
        return "-";
    }

    public enum Status {
        SCHEDULED, ON_TIME, DELAYED, CANCELLED, DEPARTED, ARRIVED
    }
}
