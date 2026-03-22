package com.schoolmanagement.schoolbackend.controller;

import java.time.LocalDate;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.schoolmanagement.schoolbackend.dto.StudentFeeDTO;
import com.schoolmanagement.schoolbackend.service.StudentFeeService;

import lombok.Data;

@RestController
@RequestMapping("/api/fees/student-fees")
public class StudentFeeMappingController {

	
	@Autowired
    private StudentFeeService studentFeeService;

    // 1. Auto-Assign Mandatory Fees (e.g., Tuition, Library)
    // Call this immediately after Student Admission
    @PostMapping("/assign-mandatory")
    public ResponseEntity<?> assignMandatoryFees(@RequestBody Map<String, Long> payload) {
        Long studentId = payload.get("studentId");
        Long classId = payload.get("classId");
        Long sessionId = payload.get("sessionId");

        studentFeeService.assignMandatoryFees(studentId, classId, sessionId);
        return ResponseEntity.ok("Mandatory fees assigned successfully.");
    }

    // 2. Get Fee Options (For the Fee Mapping Screen)
    // Shows which fees are checked (assigned) and which are unchecked
    @GetMapping("/options/{studentId}")
    public ResponseEntity<List<StudentFeeDTO>> getFeeOptions(
            @PathVariable Long studentId,
            @RequestParam Long classId,
            @RequestParam Long sessionId) {
        
        return ResponseEntity.ok(studentFeeService.getFeeOptionsForStudent(studentId, classId, sessionId));
    }

    // 3. Toggle a Fee (Add or Remove)
    // Call this when the admin clicks a checkbox
 // 3. Toggle a Fee (Updated to use DTO)
    @PostMapping("/toggle")
    public ResponseEntity<?> toggleFee(@RequestBody ToggleFeeRequest request) {
        // Now we don't need risky casting like (long) or .toString()
        studentFeeService.updateFeeStatus(
            request.getStudentId(), 
            request.getFeeStructureId(), 
            request.getIsActive(),
            request.getStartDate(), // Pass Start Date
            request.getEndDate()    // Pass End Date
        );
        return ResponseEntity.ok(Map.of("message", "Fee updated successfully"));
    }

    // === Helper DTO Class ===
    // You can put this in a separate file, or just here at the bottom for now
    @Data
    static class ToggleFeeRequest {

		private Long studentId;
        private Long feeStructureId;
        private Boolean isActive;
        private LocalDate startDate; 
        private LocalDate endDate;
    }
}
