package com.schoolmanagement.schoolbackend.dto;

import com.schoolmanagement.schoolbackend.enums.FeeFrequency;

import lombok.Data;

@Data
public class StudentFeeDTO {
    // Mapping ID (Active record ID)
    private Long mappingId; 
    
    // Fee Structure Details
    private Long feeStructureId;
    private String feeHeadName;  // e.g., "Tuition Fee"
    private FeeFrequency frequency;    // e.g., "Monthly"
    private Double amount;
    private Boolean isMandatory; // e.g., true/false
    
    // Status
    private boolean isAssigned;  // Is this fee currently assigned to the student?
}