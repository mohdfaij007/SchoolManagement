package com.schoolmanagement.schoolbackend.controller;

import com.schoolmanagement.schoolbackend.dto.ExamDTO;
import com.schoolmanagement.schoolbackend.dto.ExamSubjectMappingDTO;
import com.schoolmanagement.schoolbackend.model.Exam;
import com.schoolmanagement.schoolbackend.model.ExamSubjectMapping;
import com.schoolmanagement.schoolbackend.repository.ExamRepository;
import com.schoolmanagement.schoolbackend.service.impl.ExamServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/exams")
public class ExamController {

    @Autowired
    private ExamServiceImpl examService;
    
    @Autowired
    private ExamRepository examRepository;

    // 1. Create a new Exam (Standard Way: Returns DTO)
    @PostMapping
    public ResponseEntity<ExamDTO> createExam(@RequestBody ExamDTO examDTO) {
        // Step 1: Save using Service
        Exam savedExam = examService.createExam(examDTO);

        // Step 2: Map Entity -> DTO (To prevent Lazy Loading Issues)
        ExamDTO responseDTO = new ExamDTO();
        responseDTO.setId(savedExam.getId());
        responseDTO.setExamName(savedExam.getExamName());
        responseDTO.setDescription(savedExam.getDescription());
        responseDTO.setActive(savedExam.isActive());
        responseDTO.setAcademicSessionId(savedExam.getAcademicSession().getId());

        // Step 3: Return DTO
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    // 2. Map a Subject to an Exam (Standard Way: Returns DTO)
    @PostMapping("/map-subject")
    public ResponseEntity<?> mapSubjectToExam(@RequestBody ExamSubjectMappingDTO dto) {
        try {
            // Step 1: Save using Service
            ExamSubjectMapping mapping = examService.configureExamSubject(dto);

            // Step 2: Map Entity -> DTO
            ExamSubjectMappingDTO responseDTO = new ExamSubjectMappingDTO();
            responseDTO.setId(mapping.getId());
            responseDTO.setExamId(mapping.getExam().getId());
            responseDTO.setStandardId(mapping.getStandard().getId());
            responseDTO.setSubjectId(mapping.getSubject().getId());
            
            // Important: Send names for UI display
            responseDTO.setSubjectName(mapping.getSubject().getSubjectName()); 

            responseDTO.setMaxMarks(mapping.getMaxMarks());
            responseDTO.setPassingMarks(mapping.getPassingMarks());
            responseDTO.setExamDate(mapping.getExamDate());
            responseDTO.setStartTime(mapping.getStartTime());
            responseDTO.setEndTime(mapping.getEndTime());

            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 3. Get all configured subjects (Already returns DTO from Service)
    @GetMapping("/{examId}/class/{standardId}/subjects")
    public ResponseEntity<List<ExamSubjectMappingDTO>> getExamSubjects(
            @PathVariable Long examId, 
            @PathVariable Long standardId) {
        
        // Service layer mein humne pehle hi DTO conversion likh diya tha
        List<ExamSubjectMappingDTO> subjects = examService.getExamSubjectsForClass(examId, standardId);
        return ResponseEntity.ok(subjects);
    }
    
    
    
 // 4. Get All Active Exams (Add this inside ExamController)
    @GetMapping
    public ResponseEntity<List<ExamDTO>> getAllExams() {
        List<ExamDTO> exams = examRepository.findAll().stream().map(exam -> {
            ExamDTO dto = new ExamDTO();
            dto.setId(exam.getId());
            dto.setExamName(exam.getExamName());
            dto.setDescription(exam.getDescription());
            dto.setActive(exam.isActive());
            dto.setAcademicSessionId(exam.getAcademicSession().getId());
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(exams);
    }
}