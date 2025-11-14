package com.example.issairline.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "passengers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "passenger_id")
    private Long id;

    @NotBlank(message = "Фамилия обязательна")
    @Size(max = 50)
    private String lastName;

    @NotBlank(message = "Имя обязательно")
    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String middleName;

    @NotBlank(message = "Номер паспорта обязателен")
    @Size(max = 15)
    private String passportNumber;

    @NotBlank(message = "Номер билета обязателен")
    @Size(max = 20)
    private String ticketNumber;

    @NotBlank(message = "Место обязательно")
    @Size(max = 5)
    private String seat;

    @ManyToOne
    @JoinColumn(name = "flight_id")
    private Flight flight;
}
