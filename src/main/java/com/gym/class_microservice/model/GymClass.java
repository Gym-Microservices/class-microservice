package com.gym.class_microservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Data
@Entity
@Table(name = "classes")
@Schema(description = "Entity that represents a gym class")
public class GymClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the class", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Name of the class", example = "Morning Yoga", required = true)
    private String name;

    @Column(nullable = false)
    @Schema(description = "Scheduled date and time for the class", example = "2024-01-15T10:00:00", required = true)
    private LocalDateTime schedule;

    @Column(name = "max_capacity")
    @Schema(description = "Maximum capacity of participants", example = "20")
    private int maxCapacity;

    @Column(name = "current_enrollment")
    @Schema(description = "Current number of enrolled members", example = "15")
    private int currentEnrollment = 0;

    @Column(name = "coach_id")
    @Schema(description = "ID of the coach assigned to the class", example = "1")
    private Long coachId;

    @ElementCollection
    @CollectionTable(name = "class_enrollments", joinColumns = @JoinColumn(name = "class_id"))
    @Column(name = "member_id")
    @Schema(description = "List of member IDs enrolled in the class")
    private List<Long> enrolledMembers = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "class_equipment_reservations", joinColumns = @JoinColumn(name = "class_id"))
    @Column(name = "equipment_id")
    @Schema(description = "List of equipment IDs reserved for the class")
    private List<Long> reservedEquipment = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "class_equipment_quantities", joinColumns = @JoinColumn(name = "class_id"))
    @Column(name = "quantity")
    @Schema(description = "List of quantities corresponding to each reserved equipment")
    private List<Integer> equipmentQuantities = new ArrayList<>();
}
