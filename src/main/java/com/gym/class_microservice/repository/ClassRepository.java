package com.gym.class_microservice.repository;

import com.gym.class_microservice.model.GymClass;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClassRepository extends JpaRepository<GymClass, Long> {
    
    List<GymClass> findByCoachId(Long coachId);
    
    List<GymClass> findByScheduleBetween(LocalDateTime start, LocalDateTime end);
}
