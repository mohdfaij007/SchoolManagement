package com.schoolmanagement.schoolbackend.dto;

import java.util.List;

import com.schoolmanagement.schoolbackend.enums.QuestionType;

import lombok.Data;
@Data
public class PaperQuestionDTO {
	
	 private Long questionId;        // NULL if the teacher typed a brand new question, populated if dragged from bank
	    private String questionText;
	    private QuestionType questionType;
	    private Integer marks;          // Marks assigned for this specific paper
	    private Integer sequenceOrder;  // e.g., 1, 2, 3 (for PDF ordering)
	    private List<String> options;   // For MCQs (A, B, C, D)

}
