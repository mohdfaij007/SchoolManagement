package com.schoolmanagement.schoolbackend.dto;

import lombok.Data;
import java.util.List;

@Data
public class GeneratedPaperDTO {
    private Long schoolProfileId;
    private Long academicSessionId;
    private Long standardId;
    private Long subjectId;
    
    private String title;           // e.g., "Term 1 Science Set A"
    private Integer totalMarks;
    private Integer durationMinutes;
    
    // The list of questions the teacher added to the paper
    private List<PaperQuestionDTO> questions;
}
