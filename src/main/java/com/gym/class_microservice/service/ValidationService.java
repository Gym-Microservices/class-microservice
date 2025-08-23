package com.gym.class_microservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class ValidationService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String COACH_SERVICE_URL = "http://coach-microservice:8082";
    private static final String MEMBER_SERVICE_URL = "http://member-microservice:8081";
    private static final String EQUIPMENT_SERVICE_URL = "http://equipment-microservice:8083";


    public boolean validateCoachExists(Long coachId) {
        return exists(COACH_SERVICE_URL + "/api/coaches/" + coachId);
    }

    public boolean validateMemberExists(Long memberId) {
        return exists(MEMBER_SERVICE_URL + "/api/members/" + memberId);
    }

    public boolean validateEquipmentExists(Long equipmentId) {
        return exists(EQUIPMENT_SERVICE_URL + "/api/equipment/" + equipmentId);
    }

    public boolean reserveEquipment(Long equipmentId, int quantity) {
        return post(EQUIPMENT_SERVICE_URL + "/api/equipment/" + equipmentId + "/reserve/" + quantity);
    }

    public boolean returnEquipment(Long equipmentId, int quantity) {
        return post(EQUIPMENT_SERVICE_URL + "/api/equipment/" + equipmentId + "/return/" + quantity);
    }

    private boolean exists(String url) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException.NotFound e) {
            // Si devuelve 404 -> no existe
            return false;
        } catch (Exception e) {
            System.err.println("Error validando existencia en " + url + ": " + e.getMessage());
            return false;
        }
    }

    private boolean post(String url) {
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.err.println("Error ejecutando POST en " + url + ": " + e.getMessage());
            return false;
        }
    }
}
