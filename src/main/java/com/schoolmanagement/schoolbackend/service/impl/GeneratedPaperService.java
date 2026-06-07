package com.schoolmanagement.schoolbackend.service.impl;

import com.schoolmanagement.schoolbackend.dto.GeneratedPaperDTO;
import com.schoolmanagement.schoolbackend.dto.PaperQuestionDTO;
import com.schoolmanagement.schoolbackend.model.*;
import com.schoolmanagement.schoolbackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GeneratedPaperService {

    private final GeneratedPaperRepository paperRepository;
    private final QuestionRepository questionRepository;
    private final SchoolProfileRepository schoolRepository;
    private final AcademicSessionRepository sessionRepository;
    private final StandardRepository standardRepository;
    private final SubjectRepository subjectRepository;

    @Transactional
    public GeneratedPaper savePaperAndExtractQuestions(GeneratedPaperDTO dto) {
        
        // 1. Fetch Master Entities
        SchoolProfile school = schoolRepository.findById(dto.getSchoolProfileId()).orElseThrow();
        AcademicSession session = sessionRepository.findById(dto.getAcademicSessionId()).orElseThrow();
        Standard standard = standardRepository.findById(dto.getStandardId()).orElseThrow();
        Subject subject = subjectRepository.findById(dto.getSubjectId()).orElseThrow();

        // 2. Create the Paper Header
        GeneratedPaper paper = new GeneratedPaper();
        paper.setSchoolProfile(school);
        paper.setAcademicSession(session);
        paper.setStandard(standard);
        paper.setSubject(subject);
        paper.setTitle(dto.getTitle());
        paper.setTotalMarks(dto.getTotalMarks());
        paper.setDurationMinutes(dto.getDurationMinutes());

        // 3. Loop through incoming questions and process them
        for (PaperQuestionDTO qDto : dto.getQuestions()) {
            Question question;

            if (qDto.getQuestionId() != null) {
                // Scenario A: Teacher dragged an existing question from the bank
                question = questionRepository.findById(qDto.getQuestionId())
                        .orElseThrow(() -> new RuntimeException("Question not found!"));
            } else {
                // Scenario B: Teacher typed a brand new question (Populate the Shadow Bank!)
                question = new Question();
                question.setSchoolProfile(school);
                question.setStandard(standard);
                question.setSubject(subject);
                question.setQuestionText(qDto.getQuestionText());
                question.setQuestionType(qDto.getQuestionType());
                question.setDefaultMarks(qDto.getMarks());
                question.setOptions(qDto.getOptions());
                
                // Save to DB immediately so we can link it to the paper
                question = questionRepository.save(question); 
            }

            // 4. Map the question to the paper with its order and assigned marks
            GeneratedPaperQuestion mapping = new GeneratedPaperQuestion();
            mapping.setQuestion(question);
            mapping.setQuestionOrder(qDto.getSequenceOrder());
            mapping.setAssignedMarks(qDto.getMarks());
            
            // Link them together
            paper.addPaperQuestion(mapping);
        }

        // 5. Save the entire paper (Cascade.ALL will save the mapping rows automatically)
        return paperRepository.save(paper);
    }
}