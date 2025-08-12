package com.gym.class_microservice.repository;

import com.gym.class_microservice.model.Class;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClassRepository extends JpaRepository<Class, Long> {
    
    List<Class> findByCoachId(Long coachId);
    
    List<Class> findByScheduleBetween(LocalDateTime start, LocalDateTime end);
}
