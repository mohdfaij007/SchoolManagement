package com.schoolmanagement.schoolbackend.dto;

import lombok.Data;

@Data
public class ExamDTO {
    private Long id;
    private String examName;
    private Long academicSessionId;
    private boolean isActive;
    private String description;
}