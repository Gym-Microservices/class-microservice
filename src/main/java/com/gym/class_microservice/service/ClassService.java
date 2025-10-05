package com.gym.class_microservice.service;

import com.gym.class_microservice.dto.NotificationDTO;
import com.gym.class_microservice.model.GymClass;
import com.gym.class_microservice.repository.ClassRepository;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ClassService {
    
    @Autowired
    private ClassRepository classRepository;
    
    @Autowired
    private ValidationService validationService;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
    private ClassOccupationProducer occupationProducer;
    
    public GymClass scheduleClass(GymClass classObj) {
        if (classObj.getSchedule() == null) {
            throw new RuntimeException("Class schedule cannot be null");
        }
        
        if (classObj.getMaxCapacity() <= 0) {
            throw new RuntimeException("Class max capacity must be greater than 0");
        }
        
        // Validar que el coach existe
        if (classObj.getCoachId() != null && !validationService.validateCoachExists(classObj.getCoachId())) {
            throw new RuntimeException("Coach with id " + classObj.getCoachId() + " does not exist");
        }
        
        // Validar y reservar equipos si se especifican
        if (classObj.getReservedEquipment() != null && !classObj.getReservedEquipment().isEmpty()) {
            for (int i = 0; i < classObj.getReservedEquipment().size(); i++) {
                Long equipmentId = classObj.getReservedEquipment().get(i);
                Integer quantity = classObj.getEquipmentQuantities().get(i);
                
                if (!validationService.validateEquipmentExists(equipmentId)) {
                    throw new RuntimeException("Equipment with id " + equipmentId + " does not exist");
                }
                
                if (!validationService.reserveEquipment(equipmentId, quantity)) {
                    throw new RuntimeException("Failed to reserve equipment with id " + equipmentId);
                }
            }
        }
        
        classObj.setCurrentEnrollment(0);
        return classRepository.save(classObj);
    }
    
    public List<GymClass> getAllClasses() {
        return classRepository.findAll();
    }
    
    public Optional<GymClass> getClassById(Long id) {
        return classRepository.findById(id);
    }
    
    public List<GymClass> getClassesByCoach(Long coachId) {
        return classRepository.findByCoachId(coachId);
    }
    
    public List<GymClass> getClassesByDateRange(LocalDateTime start, LocalDateTime end) {
        return classRepository.findByScheduleBetween(start, end);
    }
    
    public List<GymClass> getClassesByMember(Long memberId) {
        return classRepository.findByEnrolledMembersContaining(memberId);
    }
    
    public GymClass updateClass(Long id, GymClass classDetails) {
        GymClass classObj = classRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + id));
        
        // Validar que el coach existe si se está cambiando
        if (classDetails.getCoachId() != null && !classDetails.getCoachId().equals(classObj.getCoachId())) {
            if (!validationService.validateCoachExists(classDetails.getCoachId())) {
                throw new RuntimeException("Coach with id " + classDetails.getCoachId() + " does not exist");
            }
        }
        
        classObj.setName(classDetails.getName());
        classObj.setMaxCapacity(classDetails.getMaxCapacity());
        classObj.setCoachId(classDetails.getCoachId());

        if (classDetails.getSchedule() != null){
            classObj.setSchedule(classDetails.getSchedule());
            sendNotification(new NotificationDTO(classObj.getCoachId(), "Class schedule updated"));
            classObj.getEnrolledMembers().forEach(memberId -> {
                sendNotification(new NotificationDTO(memberId, "Class schedule updated"));
            });
        }
        
        return classRepository.save(classObj);
    }

    public void sendNotification(NotificationDTO notification) {
        rabbitTemplate.convertAndSend("notification.exchange", "notification.key", notification);
    }
    
    public GymClass enrollMember(Long classId, Long memberId) {
        GymClass classObj = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + classId));
        
        // Validar que el miembro existe
        if (!validationService.validateMemberExists(memberId)) {
            throw new RuntimeException("Member with id " + memberId + " does not exist");
        }
        
        if (classObj.getCurrentEnrollment() >= classObj.getMaxCapacity()) {
            throw new RuntimeException("Class is at maximum capacity");
        }
        
        if (!classObj.getEnrolledMembers().contains(memberId)) {
            classObj.getEnrolledMembers().add(memberId);
            classObj.setCurrentEnrollment(classObj.getCurrentEnrollment() + 1);
            
            // Enviar actualización de ocupación a Kafka
            occupationProducer.sendOccupationUpdate(classId, classObj.getCurrentEnrollment());
        }
        
        return classRepository.save(classObj);
    }
    
    public GymClass unenrollMember(Long classId, Long memberId) {
        GymClass classObj = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + classId));
        
        if (classObj.getEnrolledMembers().remove(memberId)) {
            classObj.setCurrentEnrollment(classObj.getCurrentEnrollment() - 1);
            
            // Enviar actualización de ocupación a Kafka
            occupationProducer.sendOccupationUpdate(classId, classObj.getCurrentEnrollment());
        }
        
        return classRepository.save(classObj);
    }
    
    public GymClass reserveEquipmentForClass(Long classId, Long equipmentId, int quantity) {
        GymClass classObj = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + classId));
        
        // Validar que el equipo existe
        if (!validationService.validateEquipmentExists(equipmentId)) {
            throw new RuntimeException("Equipment with id " + equipmentId + " does not exist");
        }
        
        // Reservar el equipo
        if (!validationService.reserveEquipment(equipmentId, quantity)) {
            throw new RuntimeException("Failed to reserve equipment with id " + equipmentId);
        }
        
        // Agregar a la lista de equipos reservados
        if (!classObj.getReservedEquipment().contains(equipmentId)) {
            classObj.getReservedEquipment().add(equipmentId);
            classObj.getEquipmentQuantities().add(quantity);
        }
        
        return classRepository.save(classObj);
    }
    
    public void deleteClass(Long id) {
        GymClass classObj = classRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + id));
        
        // Devolver equipos reservados
        if (classObj.getReservedEquipment() != null && !classObj.getReservedEquipment().isEmpty()) {
            for (int i = 0; i < classObj.getReservedEquipment().size(); i++) {
                Long equipmentId = classObj.getReservedEquipment().get(i);
                Integer quantity = classObj.getEquipmentQuantities().get(i);
                validationService.returnEquipment(equipmentId, quantity);
            }
        }
        
        classRepository.deleteById(id);
    }
}
