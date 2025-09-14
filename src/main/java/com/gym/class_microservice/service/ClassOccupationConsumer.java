package com.gym.class_microservice.service;

import com.gym.class_microservice.model.ClassOccupation;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ClassOccupationConsumer {
    
    @KafkaListener(topics = "class-occupation", groupId = "gym-microservices")
    public void processOccupationUpdate(ClassOccupation occupationUpdate) {
        try {
            System.out.println("=== ACTUALIZACIÓN DE OCUPACIÓN RECIBIDA ===");
            System.out.println("ID de Clase: " + occupationUpdate.getClassId());
            System.out.println("Ocupación Actual: " + occupationUpdate.getCurrentOccupation());
            System.out.println("Timestamp: " + occupationUpdate.getTimestamp());
            System.out.println("==========================================");
            
            // Aquí puedes agregar lógica adicional para procesar la actualización
            // Por ejemplo: notificaciones, alertas, actualizaciones en base de datos, etc.
            
            processRealTimeUpdate(occupationUpdate);
            
        } catch (Exception e) {
            System.err.println("Error procesando actualización de ocupación: " + e.getMessage());
        }
    }
    
    private void processRealTimeUpdate(ClassOccupation occupationUpdate) {
        // Implementación sencilla para procesar actualizaciones en tiempo real
        Long classId = occupationUpdate.getClassId();
        Integer currentOccupation = occupationUpdate.getCurrentOccupation();
        
        // Simular procesamiento en tiempo real
        if (currentOccupation >= 10) {
            System.out.println("⚠️  ALERTA: La clase " + classId + " está casi llena (" + currentOccupation + " miembros)");
        } else if (currentOccupation == 0) {
            System.out.println("ℹ️  INFO: La clase " + classId + " está vacía");
        } else {
            System.out.println("✅ INFO: Clase " + classId + " con " + currentOccupation + " miembros inscritos");
        }
        
        // Aquí podrías agregar más lógica como:
        // - Enviar notificaciones push
        // - Actualizar métricas en tiempo real
        // - Generar reportes automáticos
        // - Integrar con sistemas de monitoreo
    }
}
