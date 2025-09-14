package com.gym.class_microservice.service;

import com.gym.class_microservice.model.ClassOccupation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ClassOccupationProducer {
    
    private static final String TOPIC = "class-occupation";
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    public void sendOccupationUpdate(Long classId, Integer currentOccupation) {
        ClassOccupation occupationUpdate = new ClassOccupation(classId, currentOccupation);
        
        try {
            kafkaTemplate.send(TOPIC, classId.toString(), occupationUpdate);
            System.out.println("Enviada actualización de ocupación para clase " + classId + 
                             " con ocupación: " + currentOccupation);
        } catch (Exception e) {
            System.err.println("Error enviando actualización de ocupación: " + e.getMessage());
        }
    }
}
