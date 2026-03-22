package com.schoolmanagement.schoolbackend.service.impl;

import com.schoolmanagement.schoolbackend.dto.SubjectDTO;
import com.schoolmanagement.schoolbackend.model.Subject;
import com.schoolmanagement.schoolbackend.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    // 1. Create Subject (Standard: Takes DTO -> Returns DTO)
    public SubjectDTO createSubject(SubjectDTO dto) {
        // Business Logic: Check Duplicate
        if(subjectRepository.existsBySubjectName(dto.getSubjectName())) {
            throw new RuntimeException("Subject with name '" + dto.getSubjectName() + "' already exists!");
        }

        // Map DTO -> Entity
        Subject subject = new Subject();
        subject.setSubjectName(dto.getSubjectName());
        subject.setSubjectCode(dto.getSubjectCode());
        subject.setSubjectType(dto.getSubjectType());

        // Save
        Subject savedSubject = subjectRepository.save(subject);

        // Map Entity -> DTO
        return mapToDTO(savedSubject);
    }

    // 2. Get All Subjects
    public List<SubjectDTO> getAllSubjects() {
        return subjectRepository.findAll()
                .stream()
                .map(this::mapToDTO) // Method Reference for cleaner code
                .collect(Collectors.toList());
    }

    // Helper Method for Mapping (Don't repeat code)
    private SubjectDTO mapToDTO(Subject subject) {
        SubjectDTO dto = new SubjectDTO();
        dto.setId(subject.getId());
        dto.setSubjectName(subject.getSubjectName());
        dto.setSubjectCode(subject.getSubjectCode());
        dto.setSubjectType(subject.getSubjectType());
        return dto;
    }
}