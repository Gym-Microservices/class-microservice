package com.gym.class_microservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ValidationService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    private static final String COACH_SERVICE_URL = "http://localhost:8082";
    private static final String MEMBER_SERVICE_URL = "http://localhost:8081";
    private static final String EQUIPMENT_SERVICE_URL = "http://localhost:8083";
    
    public boolean validateCoachExists(Long coachId) {
        try {
            ResponseEntity<Object> response = restTemplate.getForEntity(
                COACH_SERVICE_URL + "/api/coaches/" + coachId, Object.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean validateMemberExists(Long memberId) {
        try {
            ResponseEntity<Object> response = restTemplate.getForEntity(
                MEMBER_SERVICE_URL + "/api/members/" + memberId, Object.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean validateEquipmentExists(Long equipmentId) {
        try {
            ResponseEntity<Object> response = restTemplate.getForEntity(
                EQUIPMENT_SERVICE_URL + "/api/equipment/" + equipmentId, Object.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean reserveEquipment(Long equipmentId, int quantity) {
        try {
            ResponseEntity<Object> response = restTemplate.postForEntity(
                EQUIPMENT_SERVICE_URL + "/api/equipment/" + equipmentId + "/reserve?quantity=" + quantity,
                null, Object.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean returnEquipment(Long equipmentId, int quantity) {
        try {
            ResponseEntity<Object> response = restTemplate.postForEntity(
                EQUIPMENT_SERVICE_URL + "/api/equipment/" + equipmentId + "/return?quantity=" + quantity,
                null, Object.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }
}
