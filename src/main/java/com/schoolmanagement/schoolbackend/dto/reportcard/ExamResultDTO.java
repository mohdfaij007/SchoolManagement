package com.schoolmanagement.schoolbackend.dto.reportcard;

import lombok.Data;
import java.util.List;

@Data
public class ExamResultDTO {
    private Long examId;
    private String examName;
    private List<SubjectMarkDTO> subjects;
    
    // Exam Totals
    private Double totalMaxMarks;
    private Double totalMarksObtained;
    private Double percentage;
    private String overallGrade;
}