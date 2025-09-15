package com.gym.class_microservice.repository;

import com.gym.class_microservice.model.KafkaOffset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KafkaOffsetRepository extends JpaRepository<KafkaOffset, Long> {
    KafkaOffset findByTopicAndPartition(String topic, int partition);

}

