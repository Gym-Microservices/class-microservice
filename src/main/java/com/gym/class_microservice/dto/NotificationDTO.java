package com.gym.class_microservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for sending notifications")
public class NotificationDTO implements Serializable {
    @Schema(description = "ID of the recipient user", example = "1", required = true)
    private Long userId;
    @Schema(description = "Notification message", example = "A new class has been scheduled", required = true)
    private String message;
}
