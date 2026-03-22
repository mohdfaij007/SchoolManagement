package com.schoolmanagement.schoolbackend.dto.reportcard;

import lombok.Data;
import java.util.List;

@Data
public class ReportCardDTO {
    // 1. Student Info
    private Long studentId;
    private String studentName;
    private String admissionNumber;
    private String className;
    private String sectionName;
    private String fatherName;
    private String motherName;
    private String dateOfBirth;
    private String profilePhoto; // Photo URL ya filename
    
    // 2. Academic Session
    private String sessionName;

    // 3. Exams Data (Dynamic list of exams selected by Admin)
    private List<ExamResultDTO> examResults;

    // 4. Attendance Summary
    private Integer totalWorkingDays;
    private Integer presentDays;
    private Double attendancePercentage;

    // 5. Final Remarks
    private String classTeacherRemarks;
}