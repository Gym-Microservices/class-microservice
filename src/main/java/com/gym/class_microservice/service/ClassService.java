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
    
    public Class scheduleClass(Class classObj) {
        if (classObj.getSchedule() == null) {
            throw new RuntimeException("Class schedule cannot be null");
        }
        
        if (classObj.getMaxCapacity() <= 0) {
            throw new RuntimeException("Class max capacity must be greater than 0");
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
    
    public List<Class> getAvailableClasses() {
        return classRepository.findByCurrentEnrollmentLessThanMaxCapacity();
    }
    
    public Class updateClass(Long id, Class classDetails) {
        Class classObj = classRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + id));
        
        classObj.setName(classDetails.getName());
        classObj.setSchedule(classDetails.getSchedule());
        classObj.setMaxCapacity(classDetails.getMaxCapacity());
        classObj.setCoachId(classDetails.getCoachId());
        
        return classRepository.save(classObj);
    }
    
    public Class enrollMember(Long classId, Long memberId) {
        Class classObj = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + classId));
        
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
    
    public void deleteClass(Long id) {
        classRepository.deleteById(id);
    }
}
