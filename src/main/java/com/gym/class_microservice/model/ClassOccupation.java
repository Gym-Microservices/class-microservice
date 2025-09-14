package com.gym.class_microservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassOccupation {
    
    private Long classId;
    private Integer currentOccupation;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    public ClassOccupation(Long classId, Integer currentOccupation) {
        this.classId = classId;
        this.currentOccupation = currentOccupation;
        this.timestamp = LocalDateTime.now();
    }
}
