package com.gym.class_microservice.repository;


import com.gym.class_microservice.model.Clase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaseRepository extends JpaRepository<Clase, Long> {
}