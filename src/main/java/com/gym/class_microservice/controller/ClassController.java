package com.gym.class_microservice.controller;

import com.gym.class_microservice.model.Class;
import com.gym.class_microservice.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/classes")
@CrossOrigin(origins = "*")
public class ClassController {
    
    @Autowired
    private ClassService classService;
    
    @PostMapping
    public ResponseEntity<Class> scheduleClass(@RequestBody Class classObj) {
        try {
            Class scheduledClass = classService.scheduleClass(classObj);
            return ResponseEntity.ok(scheduledClass);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<Class>> getAllClasses() {
        List<Class> classes = classService.getAllClasses();
        return ResponseEntity.ok(classes);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<Class>> getActiveClasses() {
        List<Class> activeClasses = classService.getActiveClasses();
        return ResponseEntity.ok(activeClasses);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Class> getClassById(@PathVariable Long id) {
        Optional<Class> classObj = classService.getClassById(id);
        return classObj.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/coach/{coachId}")
    public ResponseEntity<List<Class>> getClassesByCoach(@PathVariable Long coachId) {
        List<Class> classes = classService.getClassesByCoach(coachId);
        return ResponseEntity.ok(classes);
    }
    
    @GetMapping("/type/{classType}")
    public ResponseEntity<List<Class>> getClassesByType(@PathVariable String classType) {
        List<Class> classes = classService.getClassesByType(classType);
        return ResponseEntity.ok(classes);
    }
    
    @GetMapping("/schedule")
    public ResponseEntity<List<Class>> getClassesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<Class> classes = classService.getClassesByDateRange(start, end);
        return ResponseEntity.ok(classes);
    }
    
    @GetMapping("/available")
    public ResponseEntity<List<Class>> getAvailableClasses() {
        List<Class> classes = classService.getAvailableClasses();
        return ResponseEntity.ok(classes);
    }
    
    @GetMapping("/room/{roomNumber}")
    public ResponseEntity<List<Class>> getClassesByRoom(@PathVariable String roomNumber) {
        List<Class> classes = classService.getClassesByRoom(roomNumber);
        return ResponseEntity.ok(classes);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Class> updateClass(@PathVariable Long id, @RequestBody Class classDetails) {
        try {
            Class updatedClass = classService.updateClass(id, classDetails);
            return ResponseEntity.ok(updatedClass);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{id}/enroll/{memberId}")
    public ResponseEntity<Class> enrollMember(@PathVariable Long id, @PathVariable Long memberId) {
        try {
            Class classObj = classService.enrollMember(id, memberId);
            return ResponseEntity.ok(classObj);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{id}/unenroll/{memberId}")
    public ResponseEntity<Class> unenrollMember(@PathVariable Long id, @PathVariable Long memberId) {
        try {
            Class classObj = classService.unenrollMember(id, memberId);
            return ResponseEntity.ok(classObj);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateClass(@PathVariable Long id) {
        try {
            classService.deactivateClass(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClass(@PathVariable Long id) {
        classService.deleteClass(id);
        return ResponseEntity.ok().build();
    }
}
