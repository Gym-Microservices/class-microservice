package com.gym.class_microservice.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "classes")
public class Class {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private LocalDateTime schedule;
    
    @Column(name = "max_capacity")
    private int maxCapacity;
    
    @Column(name = "current_enrollment")
    private int currentEnrollment = 0;
    
    @Column(name = "coach_id")
    private Long coachId;
    
    @ElementCollection
    @CollectionTable(name = "class_enrollments", joinColumns = @JoinColumn(name = "class_id"))
    @Column(name = "member_id")
    private List<Long> enrolledMembers;
}
