package com.schoolmanagement.schoolbackend.controller;

import com.schoolmanagement.schoolbackend.dto.StudentMarksDTO;
import com.schoolmanagement.schoolbackend.model.ExamSubjectMapping;
import com.schoolmanagement.schoolbackend.model.Student;
import com.schoolmanagement.schoolbackend.model.StudentMarks;
import com.schoolmanagement.schoolbackend.repository.ExamSubjectMappingRepository;
import com.schoolmanagement.schoolbackend.repository.StudentMarksRepository;
import com.schoolmanagement.schoolbackend.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/marks")
public class MarksEntryController {

    @Autowired
    private StudentMarksRepository studentMarksRepository;

    @Autowired
    private ExamSubjectMappingRepository examMappingRepository;

    @Autowired
    private StudentRepository studentRepository;

    // 1. GET Existing Marks for a Mapping
    @GetMapping("/mapping/{mappingId}")
    public ResponseEntity<List<StudentMarksDTO>> getMarksForMapping(@PathVariable Long mappingId) {
        List<StudentMarks> marks = studentMarksRepository.findByExamSubjectMappingId(mappingId);
        
        List<StudentMarksDTO> dtos = marks.stream().map(m -> {
            StudentMarksDTO dto = new StudentMarksDTO();
            dto.setId(m.getId());
            dto.setExamSubjectMappingId(m.getExamSubjectMapping().getId());
            dto.setStudentId(m.getStudent().getId());
            dto.setStudentName(m.getStudent().getFirstName() + " " + m.getStudent().getLastName());
            dto.setAdmissionNumber(m.getStudent().getAdmissionNumber());
            dto.setMarksObtained(m.getMarksObtained());
            dto.setAbsent(m.isAbsent());
            dto.setRemarks(m.getRemarks());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // 2. POST (Bulk Save or Update Marks)
    @PostMapping("/bulk-save")
    @Transactional
    public ResponseEntity<String> saveBulkMarks(@RequestBody List<StudentMarksDTO> marksList) {
        
        if (marksList == null || marksList.isEmpty()) {
            return ResponseEntity.badRequest().body("No marks provided");
        }

        for (StudentMarksDTO dto : marksList) {
            // Check if mark already exists for this student and this mapping
            Optional<StudentMarks> existingMarkOpt = studentMarksRepository
                    .findByExamSubjectMappingIdAndStudentId(dto.getExamSubjectMappingId(), dto.getStudentId());

            StudentMarks markToSave;

            if (existingMarkOpt.isPresent()) {
                // Update existing
                markToSave = existingMarkOpt.get();
            } else {
                // Create new
                markToSave = new StudentMarks();
                
                // Fetch references (using getReferenceById is efficient as it doesn't hit DB immediately)
                ExamSubjectMapping mapping = examMappingRepository.getReferenceById(dto.getExamSubjectMappingId());
                Student student = studentRepository.getReferenceById(dto.getStudentId());
                
                markToSave.setExamSubjectMapping(mapping);
                markToSave.setStudent(student);
            }

            // Update values
            markToSave.setMarksObtained(dto.getMarksObtained());
            markToSave.setAbsent(dto.isAbsent());
            markToSave.setRemarks(dto.getRemarks());

            // Save to DB
            studentMarksRepository.save(markToSave);
        }

        return ResponseEntity.ok("Marks saved successfully for " + marksList.size() + " students.");
    }
}