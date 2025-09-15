package com.gym.class_microservice.service;


import com.gym.class_microservice.model.KafkaOffset;
import com.gym.class_microservice.repository.KafkaOffsetRepository;
import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
public class RecuperacionService {

    @Autowired
    private ConsumerFactory<String, Object> consumerFactory;

    @Autowired
    private KafkaOffsetRepository offsetRepository;

    @PostConstruct
    public void arrancarServicio() {
        // Esto arrancará el consumo en un hilo aparte
        new Thread(this::iniciarProcesamiento).start();
    }


    public void iniciarProcesamiento() {
        KafkaConsumer<String, Object> consumer = (KafkaConsumer<String, Object>) consumerFactory.createConsumer();
        consumer.subscribe(Arrays.asList("class-occupation", "training-data"));

        // Cargar últimos offsets desde H2
        Map<TopicPartition, Long> ultimoOffset = cargarUltimoOffset();
        ultimoOffset.forEach(consumer::seek);

        while (true) {
            ConsumerRecords<String, Object> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, Object> record : records) {
                procesarRecord(record); // lógica de negocio
                guardarOffset(record.topic(), record.partition(), record.offset() + 1);
            }
        }
    }

    private void procesarRecord(ConsumerRecord<String, Object> record) {
        switch (record.topic()) {
            case "class-occupation":
                // procesar ocupación de clase
                System.out.println("Ocupación: " + record.value());
                break;
            case "training-data":
                // procesar datos de entrenamiento
                System.out.println("Training: " + record.value());
                break;
            default:
                System.out.println("Mensaje de topic desconocido: " + record.topic());
        }
    }

    private void guardarOffset(String topic, int partition, long offset) {
        // Buscar si ya existe el registro para este topic/partition
        KafkaOffset existing = offsetRepository.findByTopicAndPartition(topic, partition);
        if (existing != null) {
            existing.setOffset(offset);
            offsetRepository.save(existing);
        } else {
            KafkaOffset kafkaOffset = new KafkaOffset();
            kafkaOffset.setTopic(topic);
            kafkaOffset.setPartition(partition);
            kafkaOffset.setOffset(offset);
            offsetRepository.save(kafkaOffset);
        }
    }

    private Map<TopicPartition, Long> cargarUltimoOffset() {
        Map<TopicPartition, Long> offsets = new HashMap<>();
        List<KafkaOffset> allOffsets = offsetRepository.findAll();
        for (KafkaOffset ko : allOffsets) {
            offsets.put(new TopicPartition(ko.getTopic(), ko.getPartition()), ko.getOffset());
        }
        return offsets;
    }
}
