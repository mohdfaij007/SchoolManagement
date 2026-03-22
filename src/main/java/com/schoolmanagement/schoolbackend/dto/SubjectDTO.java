package com.schoolmanagement.schoolbackend.dto;

import lombok.Data;

@Data
public class SubjectDTO {
    private Long id;
    private String subjectName;
    private String subjectCode;
    private String subjectType; // THEORY, PRACTICAL, etc.
}