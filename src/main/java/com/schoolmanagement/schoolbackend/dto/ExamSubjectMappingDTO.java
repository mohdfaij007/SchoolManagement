package com.schoolmanagement.schoolbackend.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ExamSubjectMappingDTO {
    private Long id;
    private Long examId;
    private Long standardId;
    private Long subjectId;
    
    private String subjectName; // Frontend ko dikhane ke liye
    
    private Double maxMarks;
    private Double passingMarks;
    
    private LocalDate examDate;
    private LocalTime startTime;
    private LocalTime endTime;
}