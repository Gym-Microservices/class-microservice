package com.gym.class_microservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO implements Serializable {
    private Long userId;
    private String message;
    
}
