package com.example.issairline.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(
        name = "crew_members",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"last_name", "first_name", "middle_name", "role", "flight_id"},
                name = "unique_crew_per_flight"
        )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrewMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @NotBlank(message = "Фамилия обязательна")
    @Size(max = 50, message = "Фамилия не должна превышать 50 символов")
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @NotBlank(message = "Имя обязательно")
    @Size(max = 50, message = "Имя не должно превышать 50 символов")
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Size(max = 50, message = "Отчество не должно превышать 50 символов")
    @Column(name = "middle_name", length = 50)
    private String middleName;

    @NotBlank(message = "Должность обязательна")
    @Size(max = 50, message = "Должность не должна превышать 50 символов")
    private String role;

    @Size(max = 50, message = "Квалификация не должна превышать 50 символов")
    private String qualification;

    @Min(value = 0, message = "Стаж не может быть отрицательным")
    @Max(value = 60, message = "Стаж не может превышать 60 лет")
    @Column(name = "experience_years")
    private Integer experienceYears;

    @ManyToOne
    @JoinColumn(name = "flight_id")
    private Flight flight;
}
