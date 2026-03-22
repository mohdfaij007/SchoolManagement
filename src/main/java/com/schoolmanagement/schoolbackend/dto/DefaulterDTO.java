package com.schoolmanagement.schoolbackend.dto;

import lombok.Data;

@Data
public class DefaulterDTO {
    private Long studentId;
    private String admissionNo;
    private String studentName;
    private String fatherName;
    private String contactNumber; // Crucial for following up
    private Double totalDueAmount;
}