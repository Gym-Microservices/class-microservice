package com.gym.class_microservice.controller;

import com.gym.class_microservice.model.GymClass;
import com.gym.class_microservice.service.ClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/classes")
@CrossOrigin(origins = "*")
@Tag(name = "Classes", description = "API for gym class management")
@SecurityRequirement(name = "bearer-key")
public class ClassController {

    @Autowired
    private ClassService classService;

    @PostMapping
    @Operation(summary = "Schedule new class", description = "Creates a new class in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Class created successfully"),
            @ApiResponse(responseCode = "400", description = "Error in input data")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH')")
    public ResponseEntity<GymClass> scheduleClass(@RequestBody GymClass classObj) {
        try {
            GymClass scheduledClass = classService.scheduleClass(classObj);
            return ResponseEntity.ok(scheduledClass);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @Operation(summary = "Get all classes", description = "Returns a list of all available classes")
    @ApiResponse(responseCode = "200", description = "List of classes retrieved successfully")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH') or hasRole('ROLE_MEMBER')")
    public ResponseEntity<List<GymClass>> getAllClasses() {
        List<GymClass> classes = classService.getAllClasses();
        return ResponseEntity.ok(classes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get class by ID", description = "Returns a specific class by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Class found"),
            @ApiResponse(responseCode = "404", description = "Class not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH') or hasRole('ROLE_MEMBER')")
    public ResponseEntity<GymClass> getClassById(@Parameter(description = "Class ID") @PathVariable Long id) {
        Optional<GymClass> classObj = classService.getClassById(id);
        return classObj.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/coach/{coachId}")
    @Operation(summary = "Get classes by coach", description = "Returns all classes assigned to a specific coach")
    @ApiResponse(responseCode = "200", description = "Coach's class list retrieved successfully")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH') or hasRole('ROLE_MEMBER')")
    public ResponseEntity<List<GymClass>> getClassesByCoach(
            @Parameter(description = "Coach ID") @PathVariable Long coachId) {
        List<GymClass> classes = classService.getClassesByCoach(coachId);
        return ResponseEntity.ok(classes);
    }

    @GetMapping("/schedule")
    @Operation(summary = "Get classes by date range", description = "Returns all classes scheduled within a specific date range")
    @ApiResponse(responseCode = "200", description = "Class list in date range retrieved successfully")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH') or hasRole('ROLE_MEMBER')")
    public ResponseEntity<List<GymClass>> getClassesByDateRange(
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<GymClass> classes = classService.getClassesByDateRange(start, end);
        return ResponseEntity.ok(classes);
    }

    @GetMapping("/member/{memberId}")
    @Operation(summary = "Get classes by member", description = "Returns all classes in which a specific member is enrolled")
    @ApiResponse(responseCode = "200", description = "Member's class list retrieved successfully")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH') or hasRole('ROLE_MEMBER')")
    public ResponseEntity<List<GymClass>> getClassesByMember(
            @Parameter(description = "Member ID") @PathVariable Long memberId) {
        List<GymClass> classes = classService.getClassesByMember(memberId);
        return ResponseEntity.ok(classes);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update class", description = "Updates the details of an existing class")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Class updated successfully"),
            @ApiResponse(responseCode = "404", description = "Class not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH')")
    public ResponseEntity<GymClass> updateClass(@Parameter(description = "Class ID") @PathVariable Long id,
            @RequestBody GymClass classDetails) {
        try {
            GymClass updatedClass = classService.updateClass(id, classDetails);
            return ResponseEntity.ok(updatedClass);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/enroll/{memberId}")
    @Operation(summary = "Enroll member to class", description = "Enrolls a member to a specific class")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member enrolled successfully"),
            @ApiResponse(responseCode = "400", description = "Error in enrollment")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH') or hasRole('ROLE_MEMBER')")
    public ResponseEntity<GymClass> enrollMember(@Parameter(description = "Class ID") @PathVariable Long id,
            @Parameter(description = "Member ID") @PathVariable Long memberId) {
        try {
            GymClass classObj = classService.enrollMember(id, memberId);
            return ResponseEntity.ok(classObj);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/unenroll/{memberId}")
    @Operation(summary = "Unenroll member from class", description = "Unenrolls a member from a specific class")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member unenrolled successfully"),
            @ApiResponse(responseCode = "400", description = "Error in unenrollment")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH') or hasRole('ROLE_MEMBER')")
    public ResponseEntity<GymClass> unenrollMember(@Parameter(description = "Class ID") @PathVariable Long id,
            @Parameter(description = "Member ID") @PathVariable Long memberId) {
        try {
            GymClass classObj = classService.unenrollMember(id, memberId);
            return ResponseEntity.ok(classObj);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/equipment/{equipmentId}/reserve")
    @Operation(summary = "Reserve equipment for class", description = "Reserves specific equipment for a class")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipment reserved successfully"),
            @ApiResponse(responseCode = "400", description = "Error in equipment reservation")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH')")
    public ResponseEntity<GymClass> reserveEquipmentForClass(
            @Parameter(description = "Class ID") @PathVariable Long id,
            @Parameter(description = "Equipment ID") @PathVariable Long equipmentId,
            @Parameter(description = "Quantity to reserve") @RequestParam int quantity) {
        try {
            GymClass classObj = classService.reserveEquipmentForClass(id, equipmentId, quantity);
            return ResponseEntity.ok(classObj);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete class", description = "Deletes a class from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Class deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Class not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteClass(@Parameter(description = "Class ID") @PathVariable Long id) {
        classService.deleteClass(id);
        return ResponseEntity.ok().build();
    }
}
