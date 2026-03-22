package com.schoolmanagement.schoolbackend.dto;

import lombok.Data;

@Data
public class StudentMarksDTO {
    private Long id;
    private Long examSubjectMappingId;
    private Long studentId;
    
    // Read-only fields for UI
    private String studentName; 
    private String admissionNumber;
    private String rollNumber; // Agar aapke paas baad me roll number aaye
    
    private Double marksObtained;
    private boolean isAbsent;
    private String remarks;
}