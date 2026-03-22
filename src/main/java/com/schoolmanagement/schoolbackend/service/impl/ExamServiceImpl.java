package com.schoolmanagement.schoolbackend.service.impl;

import com.schoolmanagement.schoolbackend.dto.ExamDTO;
import com.schoolmanagement.schoolbackend.dto.ExamSubjectMappingDTO;
import com.schoolmanagement.schoolbackend.model.*;
import com.schoolmanagement.schoolbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExamServiceImpl {

    @Autowired
    private ExamRepository examRepository;
    @Autowired
    private AcademicSessionRepository sessionRepository;
    @Autowired
    private StandardRepository standardRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private ExamSubjectMappingRepository examSubjectMappingRepository;

    // 1. Create a New Exam (e.g. "Term 1")
    public Exam createExam(ExamDTO examDTO) {
        AcademicSession session = sessionRepository.findById(examDTO.getAcademicSessionId())
                .orElseThrow(() -> new RuntimeException("Session not found"));

        Exam exam = new Exam();
        exam.setExamName(examDTO.getExamName());
        exam.setDescription(examDTO.getDescription());
        exam.setAcademicSession(session);
        exam.setActive(true);

        return examRepository.save(exam);
    }

    // 2. Map a Subject to an Exam for a specific Class (Exam Configuration)
    public ExamSubjectMapping configureExamSubject(ExamSubjectMappingDTO dto) {
        
        if(examSubjectMappingRepository.existsByExamIdAndStandardIdAndSubjectId(
                dto.getExamId(), dto.getStandardId(), dto.getSubjectId())) {
            throw new RuntimeException("This subject is already configured for this class in this exam.");
        }

        Exam exam = examRepository.findById(dto.getExamId())
                .orElseThrow(() -> new RuntimeException("Exam not found"));
        Standard standard = standardRepository.findById(dto.getStandardId())
                .orElseThrow(() -> new RuntimeException("Class not found"));
        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        ExamSubjectMapping mapping = new ExamSubjectMapping();
        mapping.setExam(exam);
        mapping.setStandard(standard);
        mapping.setSubject(subject);
        mapping.setMaxMarks(dto.getMaxMarks());
        mapping.setPassingMarks(dto.getPassingMarks());
        mapping.setExamDate(dto.getExamDate());
        mapping.setStartTime(dto.getStartTime());
        mapping.setEndTime(dto.getEndTime());

        return examSubjectMappingRepository.save(mapping);
    }

    // 3. Get all Subjects configured for an Exam in a specific Class
    @Transactional(readOnly = true)
    public List<ExamSubjectMappingDTO> getExamSubjectsForClass(Long examId, Long standardId) {
        List<ExamSubjectMapping> mappings = examSubjectMappingRepository.findByExamIdAndStandardId(examId, standardId);
        
        return mappings.stream().map(m -> {
            ExamSubjectMappingDTO dto = new ExamSubjectMappingDTO();
            dto.setId(m.getId());
            dto.setExamId(m.getExam().getId());
            dto.setStandardId(m.getStandard().getId());
            dto.setSubjectId(m.getSubject().getId());
            dto.setSubjectName(m.getSubject().getSubjectName()); // Populate name for UI
            dto.setMaxMarks(m.getMaxMarks());
            dto.setPassingMarks(m.getPassingMarks());
            dto.setExamDate(m.getExamDate());
            dto.setStartTime(m.getStartTime());
            dto.setEndTime(m.getEndTime());
            return dto;
        }).collect(Collectors.toList());
    }
}