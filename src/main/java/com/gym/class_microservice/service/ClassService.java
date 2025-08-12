package com.gym.class_microservice.service;

import com.gym.class_microservice.model.Class;
import com.gym.class_microservice.repository.ClassRepository;
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
    
    public Class scheduleClass(Class classObj) {
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
    
    public List<Class> getAllClasses() {
        return classRepository.findAll();
    }
    
    public Optional<Class> getClassById(Long id) {
        return classRepository.findById(id);
    }
    
    public List<Class> getClassesByCoach(Long coachId) {
        return classRepository.findByCoachId(coachId);
    }
    
    public List<Class> getClassesByDateRange(LocalDateTime start, LocalDateTime end) {
        return classRepository.findByScheduleBetween(start, end);
    }
    
    public Class updateClass(Long id, Class classDetails) {
        Class classObj = classRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + id));
        
        // Validar que el coach existe si se está cambiando
        if (classDetails.getCoachId() != null && !classDetails.getCoachId().equals(classObj.getCoachId())) {
            if (!validationService.validateCoachExists(classDetails.getCoachId())) {
                throw new RuntimeException("Coach with id " + classDetails.getCoachId() + " does not exist");
            }
        }
        
        classObj.setName(classDetails.getName());
        classObj.setSchedule(classDetails.getSchedule());
        classObj.setMaxCapacity(classDetails.getMaxCapacity());
        classObj.setCoachId(classDetails.getCoachId());
        
        return classRepository.save(classObj);
    }
    
    public Class enrollMember(Long classId, Long memberId) {
        Class classObj = classRepository.findById(classId)
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
        }
        
        return classRepository.save(classObj);
    }
    
    public Class unenrollMember(Long classId, Long memberId) {
        Class classObj = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + classId));
        
        if (classObj.getEnrolledMembers().remove(memberId)) {
            classObj.setCurrentEnrollment(classObj.getCurrentEnrollment() - 1);
        }
        
        return classRepository.save(classObj);
    }
    
    public Class reserveEquipmentForClass(Long classId, Long equipmentId, int quantity) {
        Class classObj = classRepository.findById(classId)
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
        Class classObj = classRepository.findById(id)
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
