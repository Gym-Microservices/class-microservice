package com.gym.class_microservice.service;

import com.gym.class_microservice.model.ClassOccupation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.SendResult;

@Service
public class ClassOccupationProducer {
    
    private static final String TOPIC = "class-occupation";
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassOccupationProducer.class);
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    public void sendOccupationUpdate(Long classId, Integer currentOccupation) {
        ClassOccupation occupationUpdate = new ClassOccupation(classId, currentOccupation);
        
        try {
            kafkaTemplate
                .send(TOPIC, classId.toString(), occupationUpdate)
                .whenComplete((SendResult<String, Object> result, Throwable ex) -> {
                    if (ex == null) {
                        LOGGER.info("Kafka enviado a '{}' clave={} payload={}", TOPIC, classId, occupationUpdate);
                    } else {
                        LOGGER.error("Fallo enviando a Kafka tópico '{}' clave={} error={}", TOPIC, classId, ex.getMessage());
                    }
                });
        } catch (Exception e) {
            LOGGER.error("Error enviando actualización de ocupación: {}", e.getMessage());
        }
    }
}
