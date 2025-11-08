package com.example.issairline.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Сущность "Рейс"
 * Таблица flights — хранит информацию о рейсах авиакомпании
 */
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

    @Column(name = "flight_no", nullable = false, length = 10)
    private String flightNo;

    @Column(name = "scheduled_departure", nullable = false)
    private LocalDateTime scheduledDeparture;

    @Column(name = "scheduled_arrival", nullable = false)
    private LocalDateTime scheduledArrival;

    @Column(name = "departure_airport", nullable = false, length = 50)
    private String departureAirport;

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
