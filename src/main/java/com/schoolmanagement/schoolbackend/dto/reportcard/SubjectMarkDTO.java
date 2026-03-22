package com.schoolmanagement.schoolbackend.dto.reportcard;

import lombok.Data;

@Data
public class SubjectMarkDTO {
    private String subjectName;
    private Double maxMarks;
    private Double passingMarks;
    private Double marksObtained;
    private String grade; // Automatically calculate hoga
    private String remarks;
}