package com.gym.class_microservice.controller;

import com.gym.class_microservice.model.Clase;
import com.gym.class_microservice.service.ClaseService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/clase")
public class ClaseController {

    private final ClaseService claseService;

    @PostMapping("/clases")
    public Clase programarClase(@RequestBody Clase clase) {
        return claseService.programarClase(clase);
    }

    @GetMapping("/clases")
    public List<Clase> obtenerTodasClases() {
        return claseService.obtenerTodasClases();
    }
}
