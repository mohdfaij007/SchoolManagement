package com.schoolmanagement.schoolbackend.service.impl;

import com.schoolmanagement.schoolbackend.dto.GradeMasterDTO;
import com.schoolmanagement.schoolbackend.model.GradeMaster;
import com.schoolmanagement.schoolbackend.repository.GradeMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GradeMasterService {

    @Autowired
    private GradeMasterRepository gradeRepository;

    // 1. Create Grade
    public GradeMasterDTO createGrade(GradeMasterDTO dto) {
        // --- BUSINESS VALIDATIONS ---
        
        // Rule 1: Min marks Max se zyada nahi ho sakte
        if (dto.getMinPercentage() >= dto.getMaxPercentage()) {
            throw new RuntimeException("Min Percentage must be less than Max Percentage!");
        }

        // Rule 2: Duplicate Name Check
        if (gradeRepository.existsByGradeName(dto.getGradeName())) {
            throw new RuntimeException("Grade '" + dto.getGradeName() + "' already exists!");
        }

        // --- MAPPING (DTO -> Entity) ---
        GradeMaster grade = new GradeMaster();
        grade.setGradeName(dto.getGradeName());
        grade.setMinPercentage(dto.getMinPercentage());
        grade.setMaxPercentage(dto.getMaxPercentage());
        grade.setGradePoint(dto.getGradePoint());
        grade.setRemarks(dto.getRemarks());

        GradeMaster savedGrade = gradeRepository.save(grade);

        // --- MAPPING (Entity -> DTO) ---
        return mapToDTO(savedGrade);
    }

    // 2. Get All Grades
    public List<GradeMasterDTO> getAllGrades() {
        return gradeRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Helper Mapper
    private GradeMasterDTO mapToDTO(GradeMaster grade) {
        GradeMasterDTO dto = new GradeMasterDTO();
        dto.setId(grade.getId());
        dto.setGradeName(grade.getGradeName());
        dto.setMinPercentage(grade.getMinPercentage());
        dto.setMaxPercentage(grade.getMaxPercentage());
        dto.setGradePoint(grade.getGradePoint());
        dto.setRemarks(grade.getRemarks());
        return dto;
    }
}