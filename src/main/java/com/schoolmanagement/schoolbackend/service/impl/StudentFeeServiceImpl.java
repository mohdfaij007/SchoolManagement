package com.schoolmanagement.schoolbackend.service.impl;

import java.time.LocalDate;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.schoolbackend.dto.StudentFeeDTO;
import com.schoolmanagement.schoolbackend.model.FeeStructure;
import com.schoolmanagement.schoolbackend.model.Student;
import com.schoolmanagement.schoolbackend.model.StudentFeeMapping;
import com.schoolmanagement.schoolbackend.repository.FeeStructureRepository;
import com.schoolmanagement.schoolbackend.repository.StudentFeeMappingRepository;
import com.schoolmanagement.schoolbackend.repository.StudentRepository;
import com.schoolmanagement.schoolbackend.service.StudentFeeService;

@Service
public class StudentFeeServiceImpl implements StudentFeeService {

    @Autowired
    private StudentFeeMappingRepository mappingRepository;

    @Autowired
    private FeeStructureRepository feeStructureRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Override
    @Transactional
    public void assignMandatoryFees(Long studentId, Long classId, Long sessionId) {
        List<FeeStructure> classFees = feeStructureRepository.findByStandardIdAndAcademicSessionId(classId, sessionId);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        for (FeeStructure fee : classFees) {
            if (Boolean.TRUE.equals(fee.getIsMandatory())) {
                assignFeeIfNotExists(student, fee);
            }
        }
    }

    @Override
    public List<StudentFeeDTO> getFeeOptionsForStudent(Long studentId, Long classId, Long sessionId) {
        List<FeeStructure> allClassFees = feeStructureRepository.findByStandardIdAndAcademicSessionId(classId, sessionId);
        List<StudentFeeMapping> studentMappings = mappingRepository.findByStudentId(studentId);
        List<StudentFeeDTO> resultList = new ArrayList<>();

        for (FeeStructure fee : allClassFees) {
            StudentFeeDTO dto = new StudentFeeDTO();
            dto.setFeeStructureId(fee.getId());
            dto.setFeeHeadName(fee.getFeeHead().getHeadName());
            dto.setFrequency(fee.getFeeHead().getFrequency());
            dto.setAmount(fee.getAmount());
            dto.setIsMandatory(fee.getIsMandatory());

            // 3. CHECK: Does the student have this fee?
            Optional<StudentFeeMapping> existingMapping = studentMappings.stream()
                    .filter(m -> m.getFeeStructure().getId().equals(fee.getId()))
                    .findFirst();

            if (existingMapping.isPresent()) {
                // SCENARIO A: A record exists (User has explicitly set this before)
                dto.setAssigned(existingMapping.get().isActive());
            } else {
                // SCENARIO B: No record exists -> Fallback to Class Default
                boolean defaultState = Boolean.TRUE.equals(fee.getIsMandatory());
                dto.setAssigned(defaultState);
            }
            resultList.add(dto);
        }
        return resultList;
    }

    @Override
    @Transactional // Good practice for writes
    public void updateFeeStatus(Long studentId, Long feeStructureId, Boolean isActive, LocalDate startDate, LocalDate endDate) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        FeeStructure fee = feeStructureRepository.findById(feeStructureId)
                .orElseThrow(() -> new RuntimeException("Fee Structure not found"));

        // 2. Check if mapping already exists
        Optional<StudentFeeMapping> existingOpt = mappingRepository.findByStudentIdAndFeeStructureId(studentId, feeStructureId);

        StudentFeeMapping mapping;

        if (existingOpt.isPresent()) {
            mapping = existingOpt.get();
        } else {
            // Create brand new mapping
            mapping = new StudentFeeMapping();
            mapping.setStudent(student);
            mapping.setFeeStructure(fee);
            // Default active if not specified
            mapping.setActive(isActive != null ? isActive : true);
        }

        // 3. Explicitly Set State
        if (isActive != null) {
            mapping.setActive(isActive);
        }

        // 4. Update Dates
        // Logic: If user toggles ON, they might provide dates.
        // We set them directly. This allows 'null' to clear a date if needed.
        if (Boolean.TRUE.equals(isActive)) {
            // Only update dates if we are turning it ON or updating an Active fee
            // If the frontend sends null, it means "No Limit" or "No Change". 
            // Ideally, Frontend should send the specific date or null.
            if (startDate != null) mapping.setStartDate(startDate);
            if (endDate != null) mapping.setEndDate(endDate);
        }

        mappingRepository.save(mapping);
    }

    private void assignFeeIfNotExists(Student student, FeeStructure fee) {
        Optional<StudentFeeMapping> existing = mappingRepository.findByStudentIdAndFeeStructureId(student.getId(), fee.getId());

        if (existing.isEmpty()) {
            StudentFeeMapping mapping = new StudentFeeMapping();
            mapping.setStudent(student);
            mapping.setFeeStructure(fee);
            mapping.setActive(true);
            mappingRepository.save(mapping);
        }
    }
}